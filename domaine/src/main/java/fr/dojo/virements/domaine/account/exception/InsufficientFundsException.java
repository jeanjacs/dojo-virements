package fr.dojo.virements.domaine.account.exception;

import fr.dojo.virements.domaine.common.exception.BusinessException;

public final class InsufficientFundsException extends BusinessException {
    public InsufficientFundsException() {
        super(AccountErrors.INSUFFICIENT_FUNDS, "The account balance is insufficient.");
    }
}
