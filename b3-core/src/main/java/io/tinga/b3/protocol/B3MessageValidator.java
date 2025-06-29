package io.tinga.b3.protocol;

public interface B3MessageValidator {
    public void validate(B3Message<?> message) throws B3MessageValidationException;
    public void validateUpdate(B3Message<?> from, B3Message<?> to) throws B3MessageValidationException;
}
