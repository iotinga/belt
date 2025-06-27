package io.tinga.b3.core.database.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.tinga.b3.core.database.DbConfig;

public class DbConfigImpl implements DbConfig {

    @JsonProperty(DB_USER)
    private String dbUser;

    @JsonProperty(DB_PASSWORD)
    private String dbPassword;

    @JsonProperty(DB_NAME)
    private String dbName;

    @JsonProperty(DB_HOSTNAME)
    private String hostname;

    @JsonProperty(DB_PORT)
    private int port;

    @Override
    public String getDbUrl() {
        return String.format("jdbc:postgresql://%s:%d/%s", this.hostname, this.port, this.dbName);
    }

    @Override
    public String dbUser() {
        return this.dbUser;
    }

    @Override
    public String dbPassword() {
        return this.dbPassword;
    }

    @Override
    public String dbName() {
        return this.dbName;
    }

    @Override
    public String hostname() {
        return this.hostname;
    }

    @Override
    public int port() {
        return this.port;
    }

}
