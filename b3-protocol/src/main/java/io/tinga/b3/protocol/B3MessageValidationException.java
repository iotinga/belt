package io.tinga.b3.protocol;

import java.util.List;

import io.tinga.belt.output.Status;

public class B3MessageValidationException extends Exception {

    public final Status status;
    public final List<String> reasons;

    public B3MessageValidationException(List<String> reasons, Status status) {
        this.reasons = reasons;
        this.status = status;
    }

    @Override
    public String getMessage() {
        String list = String.join(", ", reasons);
        return String.format("reasons: %s", list);
    }
}
