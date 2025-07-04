package io.tinga.b3.agent.driver.database;

public interface DbConfig {

    public static final String DB_USER = "USER";
    public static final String DB_PASSWORD = "PASSWORD";
    public static final String DB_NAME = "NAME";
    public static final String DB_HOSTNAME = "HOSTNAME";
    public static final String DB_PORT = "PORT";

    String getDbUrl();

    String hostname();

    int port();

    String dbName();

    String dbUser();

    String dbPassword();
}
