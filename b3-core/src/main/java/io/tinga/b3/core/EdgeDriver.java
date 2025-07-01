package io.tinga.b3.core;

import io.tinga.b3.core.connection.ConnectionState;
import io.tinga.b3.protocol.B3Message;
import it.netgrid.bauer.EventHandler;

public interface EdgeDriver<M extends B3Message<?>> {

    ConnectionState getConnectionState();

    void connect();

    void disconnect();

    void write(M desiredMessage) throws EdgeDriverException;

    void subscribe(EventHandler<M> reportedObserver);

    void unsubscribe(EventHandler<M> reportedObserver);

}
