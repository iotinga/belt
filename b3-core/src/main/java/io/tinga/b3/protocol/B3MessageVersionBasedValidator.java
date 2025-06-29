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

    @Override
    public void validateUpdate(B3Message<?> current, B3Message<?> incoming) throws B3MessageValidationException {

        this.validate(incoming);

        if (current != null && incoming == null) {
            // the from != null && to == null condition is considered as conflict
            // because it is not possible to verify the version owned from the requester
            // and this is a version based conflicts detector.
            throw new B3MessageValidationException("null on existing item", Status.CONFLICT);
        }

        if (current != null && incoming != null && incoming.getVersion() != 0
                && current.getVersion() != incoming.getVersion()) {
            throw new B3MessageValidationException(
                    String.format("Version missmatch %d %d", current.getVersion(), incoming.getVersion()),
                    Status.CONFLICT);
        }
    }

    public List<String> detectErrors(B3Message<?> message, List<String> errors) {
        if (message != null && message.getVersion() == null) {
            errors.add("version is null");
        }
        if (message != null && message.getTimestamp() == null) {
            errors.add("timestamp is null");
        }
        if (message != null && message.getAction() == null) {
            errors.add("action is null");
        }
        if (message != null && message.getStatus() == null) {
            errors.add("%s status is null");
        }

        return errors;
    }

}
