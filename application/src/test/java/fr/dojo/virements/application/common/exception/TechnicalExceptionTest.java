package fr.dojo.virements.application.common.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TechnicalExceptionTest {
    @Test
    void exposesItsMessageCauseCodeAndDescription() {
        var cause = new IllegalStateException("database unavailable");

        var exception = new TechnicalException("Unable to store the transfer", cause);

        assertThat(exception)
                .hasMessage("Unable to store the transfer")
                .hasCause(cause);
        assertThat(exception.getCode()).isEqualTo("err.tech.01");
        assertThat(exception.getDescription()).isEqualTo("An unexpected technical error occurred.");
    }
}
