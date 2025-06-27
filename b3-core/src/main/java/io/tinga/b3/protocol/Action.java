package io.tinga.b3.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Action {
    OPTIONS(1),
    GET(2),
    HEAD(3),
    POST(4),
    PUT(5),
    DELETE(6),
    TRACE(7);

    private final int value;

    Action(int value) {
        this.value = value;
    }
    
    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static Action fromValue(int value) {
        for (Action action : Action.values()) {
            if (action.getValue() == value) {
                return action;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
