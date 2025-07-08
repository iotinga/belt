package io.tinga.b3.agent.driver.impl;

import io.tinga.b3.agent.driver.ConnectionState;
import io.tinga.b3.agent.driver.EdgeDriverException;
import io.tinga.b3.agent.driver.impl.AbstractFsmEdgeDriver.Context;
import io.tinga.b3.agent.driver.impl.AbstractFsmEdgeDriver.State;
import io.tinga.b3.helpers.GenericB3Message;
import io.tinga.b3.protocol.B3EventHandler;
import io.tinga.b3.protocol.B3Topic;
import io.tinga.b3.protocol.TestB3TopicFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.javafaker.Faker;
import com.google.inject.Inject;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbstractFsmEdgeDriverTest {

    private static final B3Topic.Factory topicFactory = TestB3TopicFactory.instance();
    private static final Faker faker = new Faker();
    private static final String agentId = faker.lorem().word();
    private static final B3Topic.Base topicBase = topicFactory.agent(agentId);
    private static final B3Topic shadowReportedTopic = topicBase.shadow().reported().build();

    @Mock
    private GenericB3Message desiredMessage;

    @Mock
    private GenericB3Message reportedMessage;

    @Mock
    private B3EventHandler<GenericB3Message> subscriber;

    @Spy
    private DummyState connectedState = new DummyState(DummyFsm.CONNECTED_STATE);

    @Spy
    private DummyState disconnectedState = new DummyState(DummyFsm.DISCONNECTED_STATE);

    private DummyFsm sut;

    @BeforeEach
    void setUp() {
        Map<String, State<String, GenericB3Message>> stateMap = Map.of(
                DummyFsm.CONNECTED_STATE, connectedState,
                DummyFsm.DISCONNECTED_STATE, disconnectedState);
        sut = new DummyFsm(topicBase, disconnectedState, stateMap);
    }

    @Test
    void shouldHaveInitialStateJustAfterConstruction() {
        assertNotNull(sut.getCurrentState());
        verify(disconnectedState, times(1)).enter(any());
        verify(disconnectedState, times(1)).enter(any());
    }

    @Test
    void shouldInvokeOnConnectDeltaOnConnectEvent() {
        sut.connect();
        verify(disconnectedState, times(1)).onConnectDelta(any());
        verify(disconnectedState, times(0)).onDisconnectDelta(any());
        verify(disconnectedState, times(0)).onWriteDelta(any());
        verify(disconnectedState, times(0)).onEmitDelta(any());
    }

    @Test
    void shouldInvokeOnDisconnectDeltaOnDisconnectEvent() {
        sut.disconnect();
        verify(disconnectedState, times(0)).onConnectDelta(any());
        verify(disconnectedState, times(1)).onDisconnectDelta(any());
        verify(disconnectedState, times(0)).onWriteDelta(any());
        verify(disconnectedState, times(0)).onEmitDelta(any());
    }

    @Test
    void shouldInvokeOnWriteDeltaOnWriteEvent() throws EdgeDriverException {
        sut.write(desiredMessage);
        verify(disconnectedState, times(0)).onConnectDelta(any());
        verify(disconnectedState, times(0)).onDisconnectDelta(any());
        verify(disconnectedState, times(1)).onWriteDelta(any());
        verify(disconnectedState, times(0)).onEmitDelta(any());
    }

    @Test
    void shouldInvokeOnEmitDeltaOnEmitEvent() {
        disconnectedState.emit(reportedMessage);
        verify(disconnectedState, times(0)).onConnectDelta(any());
        verify(disconnectedState, times(0)).onDisconnectDelta(any());
        verify(disconnectedState, times(0)).onWriteDelta(any());
        verify(disconnectedState, times(1)).onEmitDelta(any());
    }

    @Test
    void shouldThrowWritingNullDesiredMessage() {
        assertThatThrownBy(() -> sut.write(null))
                .isInstanceOf(EdgeDriverException.class);
    }

    @Test
    void shouldExitAndEnterStatesOnStateChange() {
        sut.connect();
        verify(disconnectedState, times(1)).exit(any());
        verify(connectedState, times(1)).enter(any());
        assertEquals(connectedState.current(), sut.getCurrentState());
    }

    @Test
    void shouldSendNewReportedToSubscribers() throws Exception {
        sut.subscribe(subscriber);
        disconnectedState.emit(reportedMessage);
        verify(subscriber, times(1)).handle(shadowReportedTopic, reportedMessage);
    }

    @Test
    void shouldNotSendNewReportedToUnsubscribers() throws Exception {
        sut.subscribe(subscriber);
        sut.unsubscribe(subscriber);
        disconnectedState.emit(reportedMessage);
        verify(subscriber, times(0)).handle(shadowReportedTopic, reportedMessage);
    }

    @Test
    void simpleGettersTest() {
        assertEquals(disconnectedState.current(), sut.getCurrentState());
        assertEquals(ConnectionState.DISCONNECTED, sut.getConnectionState());
    }

    static class DummyFsm extends AbstractFsmEdgeDriver<String, GenericB3Message> {

        public static final String CONNECTED_STATE = "CONNECTED";
        public static final String DISCONNECTED_STATE = "DISCONNECTED";

        public final Map<String, State<String, GenericB3Message>> stateMap;

        @Inject
        DummyFsm(B3Topic.Base topicBase, State<String, GenericB3Message> initialState,
                Map<String, State<String, GenericB3Message>> stateMap) {
            super(topicBase, initialState);
            this.stateMap = stateMap;
        }

        @Override
        protected State<String, GenericB3Message> get(String state) {
            return stateMap.get(state);
        }
    }

    static class DummyState implements AbstractFsmEdgeDriver.State<String, GenericB3Message> {
        private final String name;

        private Context<GenericB3Message> currentContext;

        DummyState(String name) {
            this.name = name;
        }

        @Override
        public void enter(Context<GenericB3Message> context) {
            this.currentContext = context;
        }

        @Override
        public String current() {
            return name;
        }

        @Override
        public String onConnectDelta(Context<GenericB3Message> context) {
            return DummyFsm.CONNECTED_STATE;
        }

        @Override
        public String onDisconnectDelta(Context<GenericB3Message> context) {
            return DummyFsm.DISCONNECTED_STATE;
        }

        @Override
        public String onWriteDelta(Context<GenericB3Message> context) {
            return name;
        }

        @Override
        public String onEmitDelta(Context<GenericB3Message> context) {
            return name;
        }

        @Override
        public ConnectionState getConnectionState() {
            return DummyFsm.CONNECTED_STATE.equals(name) ? ConnectionState.CONNECTED : ConnectionState.DISCONNECTED;
        }

        @Override
        public void exit(Context<GenericB3Message> context) {
            this.currentContext = null;
        }

        public void emit(GenericB3Message reported) {
            Context<GenericB3Message> context = null;
            if ((context = this.currentContext) != null) {
                context.reportedEmitter().apply(reported);
            }
        }
    }
}
