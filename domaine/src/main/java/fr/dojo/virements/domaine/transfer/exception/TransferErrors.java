package fr.dojo.virements.domaine.transfer.exception;

import fr.dojo.virements.domaine.common.exception.BusinessError;

public final class TransferErrors {
    public static final BusinessError INVALID_TRANSFER = new BusinessError("err.fun.02", "Invalid transfer");

    private TransferErrors() {
    }
}
