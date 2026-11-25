package fr.dojo.virements.domaine.account.valueobject;

import fr.dojo.virements.domaine.account.exception.InsufficientFundsException;
import fr.dojo.virements.domaine.account.exception.InvalidAccountException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Balance(BigDecimal value) {
    public Balance {
        if (value == null) {
            throw new InvalidAccountException("The account balance is required.");
        }
        try {
            value = value.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new InvalidAccountException("The account balance must be expressed in cents.");
        }
        if (value.signum() < 0) {
            throw new InvalidAccountException("The account balance cannot be negative.");
        }
    }

    public Balance debit(BigDecimal amount) {
        amount = validateOperationAmount(amount);
        if (value.compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }
        return new Balance(value.subtract(amount));
    }

    public Balance credit(BigDecimal amount) {
        return new Balance(value.add(validateOperationAmount(amount)));
    }

    private static BigDecimal validateOperationAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAccountException("The account operation amount is required.");
        }
        if (amount.signum() <= 0) {
            throw new InvalidAccountException("The account operation amount must be strictly positive.");
        }
        try {
            return amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new InvalidAccountException("The account operation amount must be expressed in cents.");
        }
    }
}
