package io.tinga.b3.core.database.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.tinga.b3.core.database.DbConfig;
import io.tinga.b3.core.database.DbConnectionManager;
import jakarta.inject.Inject;

public class DbBasicConnectionManager implements DbConnectionManager {
    private static final Logger log = LoggerFactory.getLogger(DbBasicConnectionManager.class);
    private static final int RECONNECT_DELAY = 5000;

    private final DbConfig dbConfig;
    private Connection connection;
    private Timer reconnectTimer;

    @Inject
    public DbBasicConnectionManager(DbConfig dbConfig) {
        this.dbConfig = dbConfig;

        try {
            connect();
        } catch (SQLException e) {
            e.printStackTrace();
            startReconnectTimer();
        }
    }

    private void connect() throws SQLException {
        connection = DriverManager.getConnection(dbConfig.getDbUrl(), dbConfig.dbUser(), dbConfig.dbPassword());
        log.info("Connected to {}", this.dbConfig.getDbUrl());
    }

    public Connection getConnection() {
        if (connection == null || !isConnectionValid()) {
            try {
                connect();
            } catch (SQLException e) {
                startReconnectTimer();
            }
        }
        return connection;
    }

    private boolean isConnectionValid() {
        try {
            return connection != null && connection.isValid(1);
        } catch (SQLException e) {
            return false;
        }
    }

    private void startReconnectTimer() {
        log.info("Unable to connect. Retry in seconds...");

        if (reconnectTimer != null) {
            reconnectTimer.cancel();
        }

        reconnectTimer = new Timer(true);
        reconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    connect();
                } catch (SQLException e) {
                    startReconnectTimer();
                }
            }
        }, RECONNECT_DELAY);
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (reconnectTimer != null) {
            reconnectTimer.cancel();
        }
    }
}
