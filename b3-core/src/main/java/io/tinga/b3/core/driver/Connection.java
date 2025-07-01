package io.tinga.b3.core.driver;

public interface Connection {
    void connect();
    void disconnect();
    ConnectionState getConnectionState();
}
