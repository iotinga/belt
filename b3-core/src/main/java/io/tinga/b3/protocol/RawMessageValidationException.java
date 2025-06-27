package io.tinga.b3.protocol;

import java.util.ArrayList;
import java.util.List;

import io.tinga.belt.output.Status;

public class RawMessageValidationException extends Exception {

    public final Status status;
    public final List<String> reasons;

    public RawMessageValidationException(List<String> reasons, Status status) {
        this.reasons = reasons;
        this.status = status;
    }

    public RawMessageValidationException(String reason, Status status) {
        this.reasons = new ArrayList<>();
        this.reasons.add(reason);
        this.status = status;
    }

    public RawMessageValidationException(String reason) {
        this.reasons = new ArrayList<>();
        this.reasons.add(reason);
        this.status = Status.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getMessage() {
        String list = String.join(", ", reasons);
        return String.format("reasons: %s", list);
    }
}
