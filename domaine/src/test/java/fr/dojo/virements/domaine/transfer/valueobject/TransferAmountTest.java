package fr.dojo.virements.domaine.transfer.valueobject;

import fr.dojo.virements.domaine.transfer.exception.InvalidTransferException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransferAmountTest {
    @Test
    void normalizesValueToCentsAndAcceptsMaximum() {
        var amount = new TransferAmount(new BigDecimal("1000"));

        assertThat(amount.value()).isEqualByComparingTo("1000.00");
        assertThat(amount.value().scale()).isEqualTo(2);
    }

    @Test
    void rejectsMissingZeroOrNegativeValue() {
        assertThatThrownBy(() -> new TransferAmount(null))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("The amount is required.");
        assertThatThrownBy(() -> new TransferAmount(BigDecimal.ZERO))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("The amount must be strictly positive.")
                .extracting("code", "label")
                .containsExactly("err.fun.02", "Invalid transfer");
        assertThatThrownBy(() -> new TransferAmount(new BigDecimal("-1.00")))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("The amount must be strictly positive.");
    }

    @Test
    void rejectsSubCentValue() {
        assertThatThrownBy(() -> new TransferAmount(new BigDecimal("10.001")))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("The amount must be expressed in cents.");
    }

    @Test
    void rejectsValueAboveMaximum() {
        assertThatThrownBy(() -> new TransferAmount(new BigDecimal("1000.01")))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("The amount cannot exceed EUR 1,000.00.")
                .extracting("code", "label")
                .containsExactly("err.fun.02", "Invalid transfer");
    }
}
