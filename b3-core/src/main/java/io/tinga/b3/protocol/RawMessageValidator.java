package io.tinga.b3.protocol;

public interface RawMessageValidator {
    public void validate(RawMessage<?> message) throws RawMessageValidationException;
    public void validateUpdate(RawMessage<?> from, RawMessage<?> to) throws RawMessageValidationException;
}
