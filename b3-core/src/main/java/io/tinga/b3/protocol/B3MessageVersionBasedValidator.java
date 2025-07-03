package io.tinga.b3.protocol;

import java.util.ArrayList;
import java.util.List;

import io.tinga.belt.output.Status;

public class B3MessageVersionBasedValidator implements B3MessageValidator {

    public void validate(B3Message<?> message) throws B3MessageValidationException {
        List<String> errors = this.detectErrors(message, new ArrayList<>());
        if (errors.size() > 0) {
            throw new B3MessageValidationException(errors, Status.BAD_REQUEST);
        }
    }

    public List<String> detectErrors(B3Message<?> message, List<String> errors) {
        if (message != null && message.getVersion() == null) {
            errors.add("version is null");
        }
        if (message != null && message.getTimestamp() == null) {
            errors.add("timestamp is null");
        }
        if (message != null && message.getCorrelationId() == null) {
            errors.add("correlationId is null");
        }
        if (message != null && message.getStatus() == null) {
            errors.add("%s status is null");
        }

        return errors;
    }

}
