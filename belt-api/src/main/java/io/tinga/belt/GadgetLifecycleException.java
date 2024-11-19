package io.tinga.belt;

import io.tinga.belt.input.GadgetCommandExecutor;

public class GadgetLifecycleException extends Exception {

    private final static Exception DEFAULT_REASON =  new Exception("No reason provided");

    public final Exception reason;
    public final GadgetCommandExecutor<?> gadget;

    public GadgetLifecycleException(GadgetCommandExecutor<?> gadget) {
        this.gadget = gadget;
        this.reason = DEFAULT_REASON;
    }

    public GadgetLifecycleException(GadgetCommandExecutor<?> gadget, Exception reason) {
        this.gadget = gadget;
        this.reason = reason == null ? DEFAULT_REASON : reason;
    }

    public String getMessage() {
        return String.format("!!! Access this.reason for more details: %s", this.reason.getMessage());
    }
}
