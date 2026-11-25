package fr.dojo.virements.infra.jpa;

import fr.dojo.virements.application.common.exception.TechnicalException;
import fr.dojo.virements.application.transfer.port.out.AccountRepository;
import fr.dojo.virements.application.transfer.port.out.TransferRepository;
import fr.dojo.virements.application.transfer.port.out.TransferUnitOfWork;
import fr.dojo.virements.domaine.account.Account;
import fr.dojo.virements.domaine.account.valueobject.AccountNumber;
import fr.dojo.virements.domaine.account.valueobject.Balance;
import fr.dojo.virements.domaine.transfer.Transfer;
import fr.dojo.virements.domaine.transfer.valueobject.TransferAmount;
import fr.dojo.virements.domaine.transfer.valueobject.TransferId;
import fr.dojo.virements.infra.jpa.account.JpaAccountRepository;
import fr.dojo.virements.infra.jpa.transfer.JpaTransferRepository;
import fr.dojo.virements.infra.jpa.transfer.JpaTransferUnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({JpaAccountRepository.class, JpaTransferRepository.class, JpaTransferUnitOfWork.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class JpaAdaptersIntegrationTest {
    @Autowired
    AccountRepository accounts;

    @Autowired
    TransferRepository transfers;

    @Autowired
    TransferUnitOfWork unitOfWork;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM virement");
        jdbcTemplate.update("DELETE FROM compte");
    }

    @Test
    void accountAdapterSavesAndRestoresDomainAggregate() {
        accounts.save(account("ACCOUNT-1", "125.50"));

        assertThat(accounts.find(number("ACCOUNT-1")))
                .contains(account("ACCOUNT-1", "125.50"));
    }

    @Test
    void transferAdapterSavesAndRestoresHistoryInInsertionOrder() {
        var first = transfer("10.00");
        var second = transfer("20.00");

        transfers.save(first);
        transfers.save(second);

        assertThat(transfers.findAll()).containsExactly(first, second);
    }

    @Test
    void unitOfWorkCommitsAccountsAndTransferTogether() {
        accounts.save(account("SOURCE", "100.00"));
        accounts.save(account("DESTINATION", "50.00"));
        var transfer = transfer("25.00");

        unitOfWork.commit(
                account("SOURCE", "75.00"),
                account("DESTINATION", "75.00"),
                transfer);

        assertThat(accounts.find(number("SOURCE"))).get().extracting(account -> account.balance().value())
                .isEqualTo(new BigDecimal("75.00"));
        assertThat(accounts.find(number("DESTINATION"))).get().extracting(account -> account.balance().value())
                .isEqualTo(new BigDecimal("75.00"));
        assertThat(transfers.findAll()).containsExactly(transfer);
    }

    @Test
    void unitOfWorkRollsBackAccountsWhenTransferCannotBeSaved() {
        accounts.save(account("SOURCE", "100.00"));
        accounts.save(account("DESTINATION", "50.00"));
        var duplicate = transfer("25.00");
        transfers.save(duplicate);

        assertThatThrownBy(() -> unitOfWork.commit(
                account("SOURCE", "75.00"),
                account("DESTINATION", "75.00"),
                duplicate))
                .isInstanceOf(TechnicalException.class);

        assertThat(accounts.find(number("SOURCE"))).get().extracting(account -> account.balance().value())
                .isEqualTo(new BigDecimal("100.00"));
        assertThat(accounts.find(number("DESTINATION"))).get().extracting(account -> account.balance().value())
                .isEqualTo(new BigDecimal("50.00"));
        assertThat(transfers.findAll()).containsExactly(duplicate);
    }

    private Transfer transfer(String amount) {
        return new Transfer(
                TransferId.generate(),
                number("SOURCE"),
                number("DESTINATION"),
                new TransferAmount(new BigDecimal(amount)));
    }

    private Account account(String number, String balance) {
        return new Account(number(number), new Balance(new BigDecimal(balance)));
    }

    private AccountNumber number(String value) {
        return new AccountNumber(value);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class TestApplication {
    }
}
