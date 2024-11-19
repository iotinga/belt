package io.tinga.belt.output;

import java.time.LocalDateTime;

public class GadgetLogRecord {

    private LocalDateTime timestamp;
    private GadgetLogLevel level;
    private String message;
    private Object[] payload;

    public GadgetLogRecord() {
    }

    public GadgetLogRecord(String source, LocalDateTime timestamp, GadgetLogLevel level, String message, Object[]... payload) {
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.payload = payload;
    }

    LocalDateTime timestamp() {
        return this.timestamp;
    }

    GadgetLogLevel level() {
        return this.level;
    }

    String message() {
        return this.message;
    }

    Object[] payload() {
        return this.payload;
    }
}
