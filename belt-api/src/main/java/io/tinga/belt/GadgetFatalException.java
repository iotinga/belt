package io.tinga.belt;

public class GadgetFatalException extends Exception {
    public final int exitCode;
    public GadgetFatalException(int exitCode, String message) {
        super(message);
        this.exitCode = exitCode;
    }
}
