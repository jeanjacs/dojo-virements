package fr.dojo.virements.exposition.api.model;

import java.math.BigDecimal;

public record AccountResponse(String account, BigDecimal balance) {
}
