package fr.dojo.virements.domaine.account.exception;

import fr.dojo.virements.domaine.common.exception.BusinessError;

public final class AccountErrors {
    public static final BusinessError INVALID_ACCOUNT = new BusinessError("err.fun.01", "Request rejected");
    public static final BusinessError INSUFFICIENT_FUNDS = new BusinessError("err.fun.03", "Request rejected");
    public static final BusinessError ACCOUNT_NOT_FOUND = new BusinessError("err.fun.04", "Request rejected");

    private AccountErrors() {
    }
}
