package fr.dojo.virements.domaine.transfer;

import fr.dojo.virements.domaine.account.valueobject.AccountNumber;
import fr.dojo.virements.domaine.common.AggregateRoot;
import fr.dojo.virements.domaine.transfer.exception.InvalidTransferException;
import fr.dojo.virements.domaine.transfer.valueobject.TransferAmount;
import fr.dojo.virements.domaine.transfer.valueobject.TransferId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransferTest {
    private static final AccountNumber SOURCE = new AccountNumber("ACCOUNT-A");
    private static final AccountNumber DESTINATION = new AccountNumber("ACCOUNT-B");
    private static final TransferAmount AMOUNT = new TransferAmount(new BigDecimal("25.00"));

    @Test
    void exposesItsTypedIdentifierAsAggregateIdentity() {
        var id = TransferId.generate();
        AggregateRoot<TransferId> aggregate = new Transfer(id, SOURCE, DESTINATION, AMOUNT);

        assertThat(aggregate.id()).isEqualTo(id);
    }

    @Test
    void requiresEveryTypedComponent() {
        assertThatThrownBy(() -> new Transfer(null, SOURCE, DESTINATION, AMOUNT))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("The transfer identifier is required.");
        assertThatThrownBy(() -> new Transfer(TransferId.generate(), null, DESTINATION, AMOUNT))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("Source and destination accounts are required.");
        assertThatThrownBy(() -> new Transfer(TransferId.generate(), SOURCE, null, AMOUNT))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("Source and destination accounts are required.");
        assertThatThrownBy(() -> new Transfer(TransferId.generate(), SOURCE, DESTINATION, null))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("The amount is required.");
    }

    @Test
    void requiresDistinctAccounts() {
        assertThatThrownBy(() -> new Transfer(TransferId.generate(), SOURCE, SOURCE, AMOUNT))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("Source and destination accounts must be distinct.");
    }
}
