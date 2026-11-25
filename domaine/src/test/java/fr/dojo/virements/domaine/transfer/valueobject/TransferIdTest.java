package fr.dojo.virements.domaine.transfer.valueobject;

import fr.dojo.virements.domaine.transfer.exception.InvalidTransferException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransferIdTest {
    @Test
    void generatesAnIdentifier() {
        assertThat(TransferId.generate().value()).isNotNull();
    }

    @Test
    void rejectsMissingValue() {
        assertThatThrownBy(() -> new TransferId(null))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("The transfer identifier is required.");
    }
}
