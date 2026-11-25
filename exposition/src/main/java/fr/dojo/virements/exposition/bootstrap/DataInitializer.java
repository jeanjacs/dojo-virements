package fr.dojo.virements.exposition.bootstrap;

import fr.dojo.virements.application.transfer.port.out.AccountRepository;
import fr.dojo.virements.domaine.account.Account;
import fr.dojo.virements.domaine.account.valueobject.AccountNumber;
import fr.dojo.virements.domaine.account.valueobject.Balance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
class DataInitializer implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataInitializer.class);
    private final AccountRepository accounts;

    DataInitializer(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments arguments) {
        initialize("COMPTE-A", "5000.00");
        initialize("COMPTE-B", "150.00");
        LOGGER.info("Reference accounts are ready: COMPTE-A=5000.00, COMPTE-B=150.00");
    }

    private void initialize(String accountNumber, String balance) {
        var number = new AccountNumber(accountNumber);
        if (accounts.find(number).isEmpty()) {
            accounts.save(new Account(number, new Balance(new BigDecimal(balance))));
        }
    }
}
