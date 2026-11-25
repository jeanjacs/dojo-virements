package fr.dojo.virements.exposition.api.model;

import fr.dojo.virements.domaine.transfer.Transfer;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferResponse(UUID id, String source, String destination, BigDecimal amount) {
    public static TransferResponse from(Transfer transfer) {
        return new TransferResponse(
                transfer.id().value(),
                transfer.source().value(),
                transfer.destination().value(),
                transfer.amount().value());
    }
}
