package fr.dojo.virements.application.transfer.port.in;

import fr.dojo.virements.domaine.transfer.Transfer;
import fr.dojo.virements.domaine.transfer.valueobject.TransferId;

import java.math.BigDecimal;
import java.util.List;

public interface TransferUseCase {
    Transfer transfer(String source, String destination, BigDecimal amount);

    Transfer getTransfer(TransferId id);

    BigDecimal getBalance(String accountNumber);

    List<Transfer> getHistory();
}
