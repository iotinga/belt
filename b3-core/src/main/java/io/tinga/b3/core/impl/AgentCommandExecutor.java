package io.tinga.b3.core.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import io.tinga.b3.core.Agent;
import io.tinga.b3.core.EdgeDriver;
import io.tinga.b3.core.AgentInitException;
import io.tinga.b3.core.VersionSafeExecutor;
import io.tinga.b3.protocol.GenericMessage;
import io.tinga.b3.protocol.topic.AgentTopic;
import io.tinga.b3.protocol.topic.RootTopic;
import io.tinga.belt.input.GadgetCommandExecutor;
import io.tinga.belt.output.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AgentCommandExecutor<C extends Agent.Command> implements Agent<JsonNode>, GadgetCommandExecutor<C> {

    private static final Logger log = LoggerFactory.getLogger(AgentCommandExecutor.class);

    protected static final int DEFAULT_THREAD_SLEEP_MS = 3000;
    protected static final int DEFAULT_INIT_SLEEP_MIN = 500;
    protected static final int DEFAULT_INIT_SLEEP_MAX = 5000;
    protected static final int DEFAULT_INIT_SLEEP_STEP = 500;

    private final Agent.ShadowReportedPolicy<JsonNode, GenericMessage> reportedPolicy;
    private final Agent.ShadowDesiredPolicy<JsonNode, GenericMessage> desiredPolicy;
    private final EdgeDriver<JsonNode, GenericMessage> driver;

    private final RootTopic rootTopic;
    private final VersionSafeExecutor executor;

    private AgentTopic agentTopic;
    private String roleName;

    @Inject
    public AgentCommandExecutor(RootTopic rootTopic,
            Agent.ShadowReportedPolicy<JsonNode, GenericMessage> reportedPolicy,
            Agent.ShadowDesiredPolicy<JsonNode, GenericMessage> desiredPolicy, VersionSafeExecutor executor,
            EdgeDriver<JsonNode, GenericMessage> driver) {
        this.executor = executor;
        this.reportedPolicy = reportedPolicy;
        this.desiredPolicy = desiredPolicy;
        this.rootTopic = rootTopic;
        this.driver = driver;
    }

    @Override
    public synchronized void bindTo(AgentTopic agent, String roleName) {
        this.agentTopic = agent;
        this.roleName = roleName;
        this.executor.initVersion(agent);
        this.executor.safeExecute(version -> {

            Integer currentVersion = null;
            int sleepMillis = this.getInitSleepMin();

            boolean keepGoing = true;

            while (keepGoing && currentVersion == null) {
                try {
                    log.info("Waiting for version initialization: " + sleepMillis + "ms");
                    Thread.sleep(sleepMillis);
                    currentVersion = version.apply(false);
                } catch (AgentInitException exception) {
                    sleepMillis += this.getInitSleepStep();
                    sleepMillis = sleepMillis > this.getInitSleepMax() ? this.getInitSleepMax() : sleepMillis;
                    log.warn(exception.getMessage());
                } catch (InterruptedException e) {
                    keepGoing = false;
                }
            }

            if (keepGoing) {
                this.reportedPolicy.bindTo(agent, roleName);
                this.desiredPolicy.bindTo(agent, roleName);
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
                try {
                    AgentTopic agentTopic = rootTopic.agent(command.agentId());
                    bindTo(agentTopic, "#");
                    while (!Thread.currentThread().isInterrupted()) {
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
                return Status.OK;
            }
        });
        return retval;
    }

    @Override
    public AgentTopic getBoundAgentTopic() {
        return this.agentTopic;
    }

    @Override
    public String getBoundRoleName() {
        return this.roleName;
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
