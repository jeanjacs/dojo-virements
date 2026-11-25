package fr.dojo.virements.application.transfer.support;

import fr.dojo.virements.application.transfer.port.out.AccountRepository;
import fr.dojo.virements.application.transfer.port.out.TransferRepository;
import fr.dojo.virements.application.transfer.port.out.TransferUnitOfWork;
import fr.dojo.virements.domaine.account.Account;
import fr.dojo.virements.domaine.transfer.Transfer;

public final class InMemoryTransferUnitOfWork implements TransferUnitOfWork {
    private final AccountRepository accounts;
    private final TransferRepository transfers;

    public InMemoryTransferUnitOfWork(AccountRepository accounts, TransferRepository transfers) {
        this.accounts = accounts;
        this.transfers = transfers;
    }

    @Override
    public void commit(Account source, Account destination, Transfer transfer) {
        accounts.save(source);
        accounts.save(destination);
        transfers.save(transfer);
    }
}
