package io.tinga.b3.core;

import io.tinga.b3.core.connection.Connection;
import io.tinga.b3.protocol.B3Message;
import it.netgrid.bauer.EventHandler;

public interface EdgeDriver<M extends B3Message<?>> extends Connection {

    void write(M desiredMessage);

    void subscribe(EventHandler<M> reportedObserver);

    void unsubscribe(EventHandler<M> reportedObserver);

}
