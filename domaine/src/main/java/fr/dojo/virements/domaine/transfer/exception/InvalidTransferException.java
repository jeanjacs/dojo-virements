package fr.dojo.virements.domaine.transfer.exception;

import fr.dojo.virements.domaine.common.exception.BusinessException;

public final class InvalidTransferException extends BusinessException {
    public InvalidTransferException(String message) {
        super(TransferErrors.INVALID_TRANSFER, message);
    }
}
