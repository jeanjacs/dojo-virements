package fr.dojo.virements.application.transfer.port.out;

import fr.dojo.virements.domaine.account.Account;
import fr.dojo.virements.domaine.transfer.Transfer;

public interface TransferUnitOfWork {
    void commit(Account source, Account destination, Transfer transfer);
}
