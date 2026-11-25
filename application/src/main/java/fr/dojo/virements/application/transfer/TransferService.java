package fr.dojo.virements.application.transfer;

import fr.dojo.virements.application.transfer.exception.AccountNotFoundException;
import fr.dojo.virements.application.transfer.port.in.TransferUseCase;
import fr.dojo.virements.application.transfer.port.out.AccountRepository;
import fr.dojo.virements.application.transfer.port.out.TransferRepository;
import fr.dojo.virements.application.transfer.port.out.TransferUnitOfWork;
import fr.dojo.virements.domaine.account.Account;
import fr.dojo.virements.domaine.account.valueobject.AccountNumber;
import fr.dojo.virements.domaine.transfer.Transfer;
import fr.dojo.virements.domaine.transfer.valueobject.TransferAmount;
import fr.dojo.virements.domaine.transfer.valueobject.TransferId;

import java.math.BigDecimal;
import java.util.List;

public class TransferService implements TransferUseCase {
    private final AccountRepository accounts;
    private final TransferRepository transfers;
    private final TransferUnitOfWork unitOfWork;

    public TransferService(AccountRepository accounts, TransferRepository transfers,
                           TransferUnitOfWork unitOfWork) {
        this.accounts = accounts;
        this.transfers = transfers;
        this.unitOfWork = unitOfWork;
    }

    @Override
    public Transfer transfer(String source, String destination, BigDecimal amount) {
        var sourceNumber = new AccountNumber(source);
        var destinationNumber = new AccountNumber(destination);
        var transfer = new Transfer(TransferId.generate(), sourceNumber, destinationNumber,
                new TransferAmount(amount));
        var sourceAccount = getAccount(sourceNumber);
        var destinationAccount = getAccount(destinationNumber);

        unitOfWork.commit(
                sourceAccount.debit(transfer.amount().value()),
                destinationAccount.credit(transfer.amount().value()),
                transfer);
        return transfer;
    }

    @Override
    public BigDecimal getBalance(String accountNumber) {
        return getAccount(new AccountNumber(accountNumber)).balance().value();
    }

    @Override
    public List<Transfer> getHistory() {
        return transfers.findAll();
    }

    private Account getAccount(AccountNumber accountNumber) {
        return accounts.find(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber.value()));
    }
}
