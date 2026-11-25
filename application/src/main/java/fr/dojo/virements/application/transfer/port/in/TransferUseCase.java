package fr.dojo.virements.application.transfer.port.in;

import fr.dojo.virements.domaine.transfer.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferUseCase {
    Transfer transfer(String source, String destination, BigDecimal amount);

    BigDecimal getBalance(String accountNumber);

    List<Transfer> getHistory();
}
