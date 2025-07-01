package io.tinga.b3.protocol;

public interface B3MessageValidator {
    public void validate(B3Message<?> message) throws B3MessageValidationException;
}
