package io.tinga.b3.core;

import io.tinga.b3.core.driver.ConnectionState;
import io.tinga.b3.protocol.B3Message;

public interface EdgeDriver<M extends B3Message<?>> {

    ConnectionState getConnectionState();

    void connect();

    void disconnect();

    void write(M desiredMessage) throws EdgeDriverException;

    void subscribe(B3EventHandler<M> reportedObserver);

    void unsubscribe(B3EventHandler<M> reportedObserver);

}
