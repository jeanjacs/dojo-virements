package fr.dojo.virements.domaine.transfer.valueobject;

import fr.dojo.virements.domaine.transfer.exception.InvalidTransferException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record TransferAmount(BigDecimal value) {
    public static final BigDecimal MAX_VALUE = new BigDecimal("1000.00");

    public TransferAmount {
        if (value == null) {
            throw new InvalidTransferException("The amount is required.");
        }
        if (value.signum() <= 0) {
            throw new InvalidTransferException("The amount must be strictly positive.");
        }
        try {
            value = value.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new InvalidTransferException("The amount must be expressed in cents.");
        }
        if (value.compareTo(MAX_VALUE) > 0) {
            throw new InvalidTransferException("The amount cannot exceed EUR 1,000.00.");
        }
    }
}
