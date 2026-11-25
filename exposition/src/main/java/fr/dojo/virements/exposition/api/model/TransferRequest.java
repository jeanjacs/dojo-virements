package fr.dojo.virements.exposition.api.model;

import java.math.BigDecimal;

public record TransferRequest(String source, String destination, BigDecimal amount) {
}
