package io.tinga.belt;

public class GadgetFatalException extends Exception {
    public final int exitCode;
    public GadgetFatalException(int exitCode) {
        this.exitCode = exitCode;
    }
}
