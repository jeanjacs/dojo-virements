package fr.dojo.virements.application.transfer.port.out;

import fr.dojo.virements.domaine.account.Account;
import fr.dojo.virements.domaine.account.valueobject.AccountNumber;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> find(AccountNumber accountNumber);

    void save(Account account);
}
