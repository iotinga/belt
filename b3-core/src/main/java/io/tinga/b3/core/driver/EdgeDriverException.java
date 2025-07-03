package io.tinga.b3.core.driver;

public class EdgeDriverException extends Exception {

    public EdgeDriverException(String message) {
        super(message);
    }

    public EdgeDriverException(Throwable reason) {
        super(reason);
    }

    public EdgeDriverException(String message, Throwable reason) {
        super(message, reason);
    }
}
