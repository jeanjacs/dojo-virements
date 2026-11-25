package fr.dojo.virements.domaine.transfer.valueobject;

import fr.dojo.virements.domaine.transfer.exception.InvalidTransferException;

import java.util.UUID;

public record TransferId(UUID value) {
    public TransferId {
        if (value == null) {
            throw new InvalidTransferException("The transfer identifier is required.");
        }
    }

    public static TransferId generate() {
        return new TransferId(UUID.randomUUID());
    }
}
