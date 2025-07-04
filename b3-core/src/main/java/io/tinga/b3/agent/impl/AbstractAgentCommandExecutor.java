package io.tinga.b3.agent.impl;

import com.google.inject.Inject;

import io.tinga.b3.agent.Agent;
import io.tinga.b3.agent.InitializationException;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class AbstractAgentCommandExecutor<M extends B3Message<?>, C>
        implements Agent<M>, GadgetCommandExecutor<C> {

    private static final Logger log = LoggerFactory.getLogger(AbstractAgentCommandExecutor.class);

    protected static final int DEFAULT_THREAD_SLEEP_MS = 3000;
    protected static final int DEFAULT_INIT_SLEEP_MIN = 500;
    protected static final int DEFAULT_INIT_SLEEP_MAX = 5000;
    protected static final int DEFAULT_INIT_SLEEP_STEP = 500;

    protected final Agent.ShadowReportedPolicy<M> reportedPolicy;
    protected final Agent.ShadowDesiredPolicy<M> desiredPolicy;
    protected final Agent.EdgeDriver<M> driver;

    protected final VersionSafeExecutor executor;

    private B3Topic.Base topicBase;
    private String roleName;

    @Inject
    public AbstractAgentCommandExecutor(
            B3Topic.Base topicBase,
            Agent.ShadowReportedPolicy<M> reportedPolicy,
            Agent.ShadowDesiredPolicy<M> desiredPolicy, VersionSafeExecutor executor,
            Agent.EdgeDriver<M> driver) {
        this.executor = executor;
        this.reportedPolicy = reportedPolicy;
        this.desiredPolicy = desiredPolicy;
        this.driver = driver;
    }

    public abstract Status execute(C command);

    @Override
    public synchronized void bindTo(B3Topic.Base topicBase, String roleName) {
        this.topicBase = topicBase;
        this.roleName = roleName;
        this.executor.initVersion(topicBase);
        this.executor.safeExecute(version -> {

            Integer currentVersion = null;
            int sleepMillis = this.getInitSleepMin();

            boolean keepGoing = true;

            while (keepGoing && currentVersion == null) {
                try {
                    log.info("Waiting for version initialization: " + sleepMillis + "ms");
                    Thread.sleep(sleepMillis);
                    currentVersion = version.apply(false);
                } catch (InitializationException exception) {
                    sleepMillis += this.getInitSleepStep();
                    sleepMillis = sleepMillis > this.getInitSleepMax() ? this.getInitSleepMax() : sleepMillis;
                    log.warn(exception.getMessage());
                } catch (InterruptedException e) {
                    keepGoing = false;
                }
            }

            if (keepGoing) {
                this.reportedPolicy.bindTo(topicBase, roleName);
                this.desiredPolicy.bindTo(topicBase, roleName);
                this.driver.connect();
            }
            return null;

        });
    }

    @Override
    public CompletableFuture<Status> submit(C command) {
        CompletableFuture<Status> retval = new CompletableFuture<>();
        retval.completeAsync(new Supplier<Status>() {
            @Override
            public Status get() {
                Status retval = Status.OK;
                try {
                    bindTo(topicBase, "#");
                    retval = execute(command);
                    while (keepAlive()) {
                        try {
                            Thread.sleep(getThreadSleepsMs());
                        } catch (InterruptedException e) {
                            log.info("Interrupt: %s {}", e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getLocalizedMessage());
                } finally {
                    log.info("Shutdown");
                }
                return retval;
            }
        });
        return retval;
    }

    protected boolean keepAlive() {
        return !Thread.currentThread().isInterrupted();
    }

    @Override
    public B3Topic.Base getBoundTopicName() {
        return this.topicBase;
    }

    @Override
    public String getBoundRoleName() {
        return this.roleName;
    }

    protected B3Topic.Base getTopicName() {
        return topicBase;
    }

    protected String getRoleName() {
        return roleName;
    }

    protected int getThreadSleepsMs() {
        return DEFAULT_THREAD_SLEEP_MS;
    }

    protected int getInitSleepMin() {
        return DEFAULT_INIT_SLEEP_MIN;
    }

    protected int getInitSleepMax() {
        return DEFAULT_INIT_SLEEP_MAX;
    }

    protected int getInitSleepStep() {
        return DEFAULT_INIT_SLEEP_STEP;
    }

}
