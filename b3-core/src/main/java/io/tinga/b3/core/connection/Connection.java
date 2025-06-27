package io.tinga.b3.core.connection;

public interface Connection {
    void connect();
    void disconnect();
    ConnectionState getConnectionState();
}
