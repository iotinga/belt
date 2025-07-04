package io.tinga.b3.agent.driver;

public interface Connection {
    void connect();
    void disconnect();
    ConnectionState getConnectionState();
}
