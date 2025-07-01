package io.tinga.b3.groupagent.driver.states;

import com.google.inject.Singleton;

import io.tinga.b3.core.driver.AbstractFsmEdgeDriver;
import io.tinga.b3.core.driver.ConnectionState;
import io.tinga.b3.core.driver.AbstractFsmEdgeDriver.Context;
import io.tinga.b3.protocol.GenericB3Message;

@Singleton
public class EdgeDriverFsmDisconnected implements AbstractFsmEdgeDriver.State<EdgeDriverFsmState, GenericB3Message> {

    private ConnectionState connectionState;

    @Override
    public void enter(Context<GenericB3Message> context) {
        this.connectionState = ConnectionState.DISCONNECTED;
    }

    @Override
    public EdgeDriverFsmState onConnectDelta(Context<GenericB3Message> context) {
        this.connectionState = ConnectionState.CONNECTING;
        return EdgeDriverFsmState.CONNECTED;
    }

    @Override
    public EdgeDriverFsmState onDisconnectDelta(
            Context<GenericB3Message> context) {
        this.connectionState = ConnectionState.DISCONNECTED;
        return this.current();
    }

    @Override
    public EdgeDriverFsmState onWriteDelta(Context<GenericB3Message> context) {
        return this.current();
    }

    @Override
    public ConnectionState getConnectionState() {
        return this.connectionState;
    }

    @Override
    public void exit(Context<GenericB3Message> context) {
    }

    @Override
    public EdgeDriverFsmState onEmitDelta(Context<GenericB3Message> context) {
        return this.current();
    }

    @Override
    public EdgeDriverFsmState current() {
        return EdgeDriverFsmState.DISCONNECTED;
    }

}
