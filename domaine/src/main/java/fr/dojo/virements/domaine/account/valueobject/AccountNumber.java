package fr.dojo.virements.domaine.account.valueobject;

import fr.dojo.virements.domaine.account.exception.InvalidAccountException;

public record AccountNumber(String value) {
    public static final int MAX_LENGTH = 64;

    public AccountNumber {
        if (value == null || value.isBlank()) {
            throw new InvalidAccountException("The account number is required.");
        }
        if (!value.equals(value.strip())) {
            throw new InvalidAccountException("The account number must not contain leading or trailing spaces.");
        }
        if (value.length() > MAX_LENGTH) {
            throw new InvalidAccountException("The account number cannot exceed 64 characters.");
        }
    }
}
