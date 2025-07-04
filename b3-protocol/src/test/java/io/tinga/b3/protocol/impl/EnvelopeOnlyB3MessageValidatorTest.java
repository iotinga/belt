package io.tinga.b3.protocol.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.tinga.b3.protocol.B3Message;
import io.tinga.b3.protocol.B3MessageValidationException;
import io.tinga.belt.output.Status;

public class EnvelopeOnlyB3MessageValidatorTest {

    private final EnvelopeOnlyB3MessageValidator validator = new EnvelopeOnlyB3MessageValidator();

    @Test
    void testValidateWithValidMessage() throws B3MessageValidationException {
        B3Message<String> msg = new B3Message<>(
            1700000000000L,
            1,
            1,
            "corr-id",
            Status.OK,
            "body"
        );

        // Deve passare senza eccezioni
        validator.validate(msg);
    }

    @Test
    void testValidateWithMissingFieldsThrowsException() {
        B3Message<String> msg = new B3Message<>();
        // Tutti i campi null → devono generare errori

        assertThatThrownBy(() -> validator.validate(msg))
            .isInstanceOf(B3MessageValidationException.class)
            .hasMessageContaining("version is null")
            .hasMessageContaining("timestamp is null")
            .hasMessageContaining("correlationId is null")
            .hasMessageContaining("status is null")
            .extracting("status")
            .isEqualTo(Status.BAD_REQUEST);
    }

    @Test
    void testDetectErrorsWithAllFieldsMissing() {
        B3Message<String> msg = new B3Message<>();

        List<String> errors = validator.detectErrors(msg, new ArrayList<>());

        assertThat(errors)
            .hasSize(4)
            .containsExactlyInAnyOrder(
                "version is null",
                "timestamp is null",
                "correlationId is null",
                "%s status is null"
            );
    }

    @Test
    void testDetectErrorsWithAllFieldsPresent() {
        B3Message<String> msg = new B3Message<>(
            1700000000000L,
            1,
            1,
            "corr-id",
            Status.OK,
            "body"
        );

        List<String> errors = validator.detectErrors(msg, new ArrayList<>());

        assertThat(errors).isEmpty();
    }

    @Test
    void testDetectErrorsWithNullMessage() {
        List<String> errors = validator.detectErrors(null, new ArrayList<>());

        // Nessun errore aggiunto
        assertThat(errors).isEmpty();
    }
}
