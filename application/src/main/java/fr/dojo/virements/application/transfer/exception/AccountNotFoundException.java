package fr.dojo.virements.application.transfer.exception;

import fr.dojo.virements.domaine.account.exception.AccountErrors;
import fr.dojo.virements.domaine.common.exception.BusinessException;

public final class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException(String accountNumber) {
        super(AccountErrors.ACCOUNT_NOT_FOUND, "Account not found: " + accountNumber);
    }
}
