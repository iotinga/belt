package io.tinga.b3.agent;

import com.google.inject.Inject;

import io.tinga.b3.agent.security.Operation;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor.CriticalSection;
import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class AbstractAgentCommandExecutor<M extends B3Message<?>, C>
        implements Agent<M>, GadgetCommandExecutor {

    private static final Logger log = LoggerFactory.getLogger(AbstractAgentCommandExecutor.class);

    protected static final String DEFAULT_BIND_ROLE_NAME = "#";

    protected static final int DEFAULT_THREAD_SLEEP_MS = 3000;
    protected static final int DEFAULT_INIT_SLEEP_MIN = 500;
    protected static final int DEFAULT_INIT_SLEEP_MAX = 5000;
    protected static final int DEFAULT_INIT_SLEEP_STEP = 500;

    protected final Agent.ShadowReportedPolicy<M> reportedPolicy;
    protected final Agent.ShadowDesiredPolicy<M> desiredPolicy;
    protected final Agent.EdgeDriver<M> driver;
    protected final AgentProxy<M> agentProxy;

    protected final VersionSafeExecutor executor;
    protected final Operation.GrantsChecker<M> grantsChecker;

    private final B3Topic.Base boundTopicBase;
    private String boundRoleName;

    @Inject
    public AbstractAgentCommandExecutor(
            AgentProxy<M> agentProxy,
            B3Topic.Base topicBase,
            Agent.ShadowReportedPolicy<M> reportedPolicy,
            Agent.ShadowDesiredPolicy<M> desiredPolicy,
            VersionSafeExecutor executor,
            Operation.GrantsChecker<M> grantsChecker,
            Agent.EdgeDriver<M> driver) {
        this.agentProxy = agentProxy;
        this.boundTopicBase = topicBase;
        this.executor = executor;
        this.reportedPolicy = reportedPolicy;
        this.desiredPolicy = desiredPolicy;
        this.driver = driver;
        this.grantsChecker = grantsChecker;
    }

    public abstract Status execute(C command);

    protected synchronized void bind(B3Topic.Base topicBase, String roleName) {

        // Prepare all the components needing the first Reported Message
        this.grantsChecker.bind(topicBase, roleName);
        this.reportedPolicy.bind(topicBase, roleName);
        this.executor.bind(topicBase, roleName);

        // This starts the reported message retrieval:
        // the message will be passed following the bind order defined in
        // the previous lines
        this.agentProxy.bind(topicBase, roleName);

        // As the executor is the last to be initialized, after its initializazion
        // we shall start to serve requests
        this.executor.safeExecute(this.bindCriticalSection(topicBase, roleName));

        boundRoleName = roleName;
    }

    protected CriticalSection bindCriticalSection(B3Topic.Base topicBase, String roleName) {
        return version -> {

            Integer currentVersion = null;
            int sleepMillis = getInitSleepMin();

            boolean keepGoing = true;

            while (keepGoing && currentVersion == null) {
                try {
                    log.info("Waiting for version initialization: " + sleepMillis + "ms");
                    Thread.sleep(sleepMillis);
                    currentVersion = version.apply(false);
                } catch (InitializationException exception) {
                    sleepMillis += getInitSleepStep();
                    sleepMillis = sleepMillis > getInitSleepMax() ? getInitSleepMax() : sleepMillis;
                    log.warn(exception.getMessage());
                } catch (InterruptedException e) {
                    keepGoing = false;
                }
            }

            // If the execution has not been interrupted,
            // we shall start to serve requests
            if (keepGoing) {
                driver.connect();
                desiredPolicy.bind(topicBase, roleName);
            }
            return null;

        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<Status> submit(Object rawCommand) {
        C commandToExecute = (C) rawCommand;
        CompletableFuture<Status> retval = new CompletableFuture<>();
        retval.completeAsync(new Supplier<Status>() {
            @Override
            public Status get() {
                Status retval = Status.OK;
                try {
                    bind(boundTopicBase, DEFAULT_BIND_ROLE_NAME);
                    retval = execute(commandToExecute);
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

    protected B3Topic.Base getBoundTopicBase() {
        return this.boundTopicBase;
    }

    protected String getBoundRoleName() {
        return this.boundRoleName;
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
