package fr.dojo.virements.application.transfer;

import fr.dojo.virements.application.transfer.exception.AccountNotFoundException;
import fr.dojo.virements.application.transfer.exception.TransferNotFoundException;
import fr.dojo.virements.application.transfer.support.InMemoryAccountRepository;
import fr.dojo.virements.application.transfer.support.InMemoryTransferRepository;
import fr.dojo.virements.application.transfer.support.InMemoryTransferUnitOfWork;
import fr.dojo.virements.domaine.account.Account;
import fr.dojo.virements.domaine.account.exception.InsufficientFundsException;
import fr.dojo.virements.domaine.account.valueobject.AccountNumber;
import fr.dojo.virements.domaine.account.valueobject.Balance;
import fr.dojo.virements.domaine.transfer.Transfer;
import fr.dojo.virements.domaine.transfer.valueobject.TransferAmount;
import fr.dojo.virements.domaine.transfer.valueobject.TransferId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransferServiceTest {
    private InMemoryAccountRepository accounts;
    private InMemoryTransferRepository transfers;
    private TransferService service;

    @BeforeEach
    void setUp() {
        accounts = new InMemoryAccountRepository(
                account("ACCOUNT-A", "100.00"),
                account("ACCOUNT-B", "25.00"));
        transfers = new InMemoryTransferRepository();
        service = new TransferService(accounts, transfers,
                new InMemoryTransferUnitOfWork(accounts, transfers));
    }

    @Test
    void transfersMoneyAndStoresTheTransferInOneUnitOfWork() {
        var transfer = service.transfer("ACCOUNT-A", "ACCOUNT-B", new BigDecimal("40.00"));

        assertThat(transfer.id()).isNotNull();
        assertThat(transfer.source().value()).isEqualTo("ACCOUNT-A");
        assertThat(transfer.destination().value()).isEqualTo("ACCOUNT-B");
        assertThat(transfer.amount().value()).isEqualByComparingTo("40.00");
        assertThat(service.getBalance("ACCOUNT-A")).isEqualByComparingTo("60.00");
        assertThat(service.getBalance("ACCOUNT-B")).isEqualByComparingTo("65.00");
        assertThat(service.getHistory()).containsExactly(transfer);
    }

    @Test
    void returnsTheHistoryProvidedByTheTransferRepository() {
        var first = transfer("ACCOUNT-A", "ACCOUNT-B", "10.00");
        var second = transfer("ACCOUNT-B", "ACCOUNT-A", "5.00");
        transfers.save(first);
        transfers.save(second);

        assertThat(service.getHistory()).containsExactly(first, second);
    }

    @Test
    void returnsTheTransferMatchingTheRequestedIdentifier() {
        var first = transfer("ACCOUNT-A", "ACCOUNT-B", "10.00");
        var requested = transfer("ACCOUNT-B", "ACCOUNT-A", "5.00");
        transfers.save(first);
        transfers.save(requested);

        assertThat(service.getTransfer(requested.id())).isEqualTo(requested);
    }

    @Test
    void rejectsAnUnknownTransferIdentifierWithoutChangingState() {
        var missing = TransferId.generate();

        assertThatThrownBy(() -> service.getTransfer(missing))
                .isInstanceOf(TransferNotFoundException.class)
                .hasMessage("Transfer not found: " + missing.value())
                .extracting("code", "label")
                .containsExactly("err.fun.05", "Transfer lookup failed");
        assertStateIsUnchanged();
    }

    @Test
    void rejectsAnUnknownSourceAccountWithoutChangingState() {
        assertThatThrownBy(() -> service.transfer("UNKNOWN", "ACCOUNT-B", new BigDecimal("10.00")))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account not found: UNKNOWN")
                .extracting("code", "label")
                .containsExactly("err.fun.04", "Request rejected");

        assertStateIsUnchanged();
    }

    @Test
    void rejectsAnUnknownDestinationAccountWithoutChangingState() {
        assertThatThrownBy(() -> service.transfer("ACCOUNT-A", "UNKNOWN", new BigDecimal("10.00")))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account not found: UNKNOWN");

        assertStateIsUnchanged();
    }

    @Test
    void rejectsInsufficientFundsWithoutChangingState() {
        assertThatThrownBy(() -> service.transfer("ACCOUNT-A", "ACCOUNT-B", new BigDecimal("100.01")))
                .isInstanceOf(InsufficientFundsException.class);

        assertStateIsUnchanged();
    }

    @Test
    void rejectsBalanceLookupForAnUnknownAccount() {
        assertThatThrownBy(() -> service.getBalance("UNKNOWN"))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account not found: UNKNOWN");
    }

    private void assertStateIsUnchanged() {
        assertThat(service.getBalance("ACCOUNT-A")).isEqualByComparingTo("100.00");
        assertThat(service.getBalance("ACCOUNT-B")).isEqualByComparingTo("25.00");
        assertThat(service.getHistory()).isEmpty();
    }

    private static Account account(String number, String balance) {
        return new Account(new AccountNumber(number), new Balance(new BigDecimal(balance)));
    }

    private static Transfer transfer(String source, String destination, String amount) {
        return new Transfer(
                TransferId.generate(),
                new AccountNumber(source),
                new AccountNumber(destination),
                new TransferAmount(new BigDecimal(amount)));
    }
}
