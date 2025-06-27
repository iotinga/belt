package io.tinga.b3.core.database;

import java.sql.Connection;

public interface DbConnectionManager {
    public Connection getConnection();

    public void close();
}