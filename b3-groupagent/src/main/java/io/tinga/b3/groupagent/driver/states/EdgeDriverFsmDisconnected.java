package io.tinga.b3.groupagent.driver.states;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Singleton;

import io.tinga.b3.core.connection.ConnectionState;
import io.tinga.b3.core.impl.AbstractFsmEdgeDriver;
import io.tinga.b3.core.impl.AbstractFsmEdgeDriver.Context;
import io.tinga.b3.protocol.GenericMessage;

@Singleton
public class EdgeDriverFsmDisconnected implements AbstractFsmEdgeDriver.State<EdgeDriverFsmState, ObjectNode, GenericMessage> {

    private ConnectionState connectionState;

    @Override
    public void enter(Context<GenericMessage> context) {
        this.connectionState = ConnectionState.DISCONNECTED;
    }

    @Override
    public EdgeDriverFsmState onConnectDelta(Context<GenericMessage> context) {
        this.connectionState = ConnectionState.CONNECTING;
        return EdgeDriverFsmState.CONNECTED;
    }

    @Override
    public EdgeDriverFsmState onDisconnectDelta(
            Context<GenericMessage> context) {
        this.connectionState = ConnectionState.DISCONNECTED;
        return this.current();
    }

    @Override
    public EdgeDriverFsmState onWriteDelta(Context<GenericMessage> context) {
        return this.current();
    }

    @Override
    public ConnectionState getConnectionState() {
        return this.connectionState;
    }

    @Override
    public void exit(Context<GenericMessage> context) {
    }

    @Override
    public EdgeDriverFsmState onEmitDelta(Context<GenericMessage> context) {
        return this.current();
    }

    @Override
    public EdgeDriverFsmState current() {
        return EdgeDriverFsmState.DISCONNECTED;
    }

}
