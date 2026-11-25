package fr.dojo.virements.domaine.transfer;

import fr.dojo.virements.domaine.account.valueobject.AccountNumber;
import fr.dojo.virements.domaine.common.AggregateRoot;
import fr.dojo.virements.domaine.transfer.exception.InvalidTransferException;
import fr.dojo.virements.domaine.transfer.valueobject.TransferAmount;
import fr.dojo.virements.domaine.transfer.valueobject.TransferId;

public record Transfer(TransferId id, AccountNumber source, AccountNumber destination, TransferAmount amount)
        implements AggregateRoot<TransferId> {

    public Transfer {
        if (id == null) {
            throw new InvalidTransferException("The transfer identifier is required.");
        }
        if (source == null || destination == null) {
            throw new InvalidTransferException("Source and destination accounts are required.");
        }
        if (source.equals(destination)) {
            throw new InvalidTransferException("Source and destination accounts must be distinct.");
        }
        if (amount == null) {
            throw new InvalidTransferException("The amount is required.");
        }
    }
}
