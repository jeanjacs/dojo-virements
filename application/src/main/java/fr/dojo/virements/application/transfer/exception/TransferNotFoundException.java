package fr.dojo.virements.application.transfer.exception;

import fr.dojo.virements.domaine.common.exception.BusinessException;
import fr.dojo.virements.domaine.transfer.exception.TransferErrors;
import fr.dojo.virements.domaine.transfer.valueobject.TransferId;

public final class TransferNotFoundException extends BusinessException {
    public TransferNotFoundException(TransferId id) {
        super(TransferErrors.TRANSFER_NOT_FOUND, "Transfer not found: " + id.value());
    }
}
