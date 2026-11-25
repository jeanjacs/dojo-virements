package fr.dojo.virements.infra.jpa.transfer;

import fr.dojo.virements.application.transfer.port.out.AccountRepository;
import fr.dojo.virements.application.transfer.port.out.TransferRepository;
import fr.dojo.virements.application.transfer.port.out.TransferUnitOfWork;
import fr.dojo.virements.domaine.account.Account;
import fr.dojo.virements.domaine.transfer.Transfer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaTransferUnitOfWork implements TransferUnitOfWork {
    private final AccountRepository accounts;
    private final TransferRepository transfers;

    JpaTransferUnitOfWork(AccountRepository accounts, TransferRepository transfers) {
        this.accounts = accounts;
        this.transfers = transfers;
    }

    @Override
    @Transactional
    public void commit(Account source, Account destination, Transfer transfer) {
        accounts.save(source);
        accounts.save(destination);
        transfers.save(transfer);
    }
}
