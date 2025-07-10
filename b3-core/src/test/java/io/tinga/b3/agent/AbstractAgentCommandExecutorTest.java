package io.tinga.b3.agent;

import io.tinga.b3.agent.security.Operation;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor;
import io.tinga.b3.agent.shadowing.VersionSafeExecutor.CriticalSection;
import io.tinga.b3.helpers.AgentProxy;
import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.TestB3TopicFactory;
import io.tinga.belt.output.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbstractAgentCommandExecutorTest {

    private static final B3Topic.Factory topicFactory = TestB3TopicFactory.instance();
    private static final Faker faker = new Faker();
    private static final String agentId = faker.lorem().word();

    @Spy
    private B3Topic.Base topicBase = topicFactory.agent(agentId);

    @Mock
    private AgentProxy<B3Message<?>> agentProxy;

    @Mock
    private Agent.ShadowReportedPolicy<B3Message<?>> reportedPolicy;

    @Mock
    private Agent.ShadowDesiredPolicy<B3Message<?>> desiredPolicy;

    @Mock
    private Agent.EdgeDriver<B3Message<?>> edgeDriver;

    @Mock
    private Operation.GrantsChecker<B3Message<?>> grantsChecker;

    @Mock
    private VersionSafeExecutor executor;

    @Spy
    @InjectMocks
    private DummyAgentCommandExecutor sut;

    @Test
    void shouldReturnBoundTopicBase() {
        assertThat(sut.getBoundTopicBase()).isEqualTo(topicBase);
    }

    @Test
    void shouldReturnBoundRoleNameInitiallyNull() {
        assertThat(sut.getBoundRoleName()).isNull();
    }

    @Test
    void shouldSubmitCommandAndReturnStatusOk() throws Exception {
        CompletableFuture<Status> future = sut.submit("command");

        Status status = future.join();

        assertThat(status).isEqualTo(Status.OK);
        verify(grantsChecker, times(1)).bind(topicBase, AbstractAgentCommandExecutor.DEFAULT_BIND_ROLE_NAME);
        verify(reportedPolicy, times(1)).bind(topicBase, AbstractAgentCommandExecutor.DEFAULT_BIND_ROLE_NAME);
        verify(executor, times(1)).bind(topicBase, AbstractAgentCommandExecutor.DEFAULT_BIND_ROLE_NAME);
        verify(agentProxy, times(1)).bind(topicBase, AbstractAgentCommandExecutor.DEFAULT_BIND_ROLE_NAME);   
        verify(sut, times(1)).bindCriticalSection(topicBase, AbstractAgentCommandExecutor.DEFAULT_BIND_ROLE_NAME);
    }

    @Test
    void shouldStartToServeRequestsAfterVersionInitialization() {
        CriticalSection section = sut.bindCriticalSection(topicBase, AbstractAgentCommandExecutor.DEFAULT_BIND_ROLE_NAME);
        section.apply(next -> next ? 2 : 1);
        verify(edgeDriver, times(1)).connect();
        verify(desiredPolicy, times(1)).bind(topicBase, AbstractAgentCommandExecutor.DEFAULT_BIND_ROLE_NAME);
    }

    static class DummyAgentCommandExecutor extends AbstractAgentCommandExecutor<B3Message<?>, String> {

        public DummyAgentCommandExecutor(AgentProxy<B3Message<?>> agentProxy,
                B3Topic.Base topicBase,
                Agent.ShadowReportedPolicy<B3Message<?>> reportedPolicy,
                Agent.ShadowDesiredPolicy<B3Message<?>> desiredPolicy,
                VersionSafeExecutor executor,
                Operation.GrantsChecker<B3Message<?>> grantsChecker,
                Agent.EdgeDriver<B3Message<?>> driver) {
            super(agentProxy, topicBase, reportedPolicy, desiredPolicy, executor, grantsChecker, driver);
        }

        @Override
        public Status execute(String command) {
            return Status.OK;
        }

        @Override
        protected int getThreadSleepsMs() {
            return 10; // speed up tests
        }

        @Override
        protected boolean keepAlive() {
            return false; // exit immediately after execution
        }
    }
}
