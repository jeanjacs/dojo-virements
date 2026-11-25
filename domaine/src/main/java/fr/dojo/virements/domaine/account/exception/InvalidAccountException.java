package fr.dojo.virements.domaine.account.exception;

import fr.dojo.virements.domaine.common.exception.BusinessException;

public final class InvalidAccountException extends BusinessException {
    public InvalidAccountException(String message) {
        super(AccountErrors.INVALID_ACCOUNT, message);
    }
}
