package fr.dojo.virements.application.transfer.support;

import fr.dojo.virements.application.transfer.port.out.AccountRepository;
import fr.dojo.virements.domaine.account.Account;
import fr.dojo.virements.domaine.account.valueobject.AccountNumber;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class InMemoryAccountRepository implements AccountRepository {
    private final Map<AccountNumber, Account> accounts = new HashMap<>();

    public InMemoryAccountRepository(Account... initialAccounts) {
        for (var account : initialAccounts) {
            save(account);
        }
    }

    @Override
    public Optional<Account> find(AccountNumber accountNumber) {
        return Optional.ofNullable(accounts.get(accountNumber));
    }

    @Override
    public void save(Account account) {
        accounts.put(account.number(), account);
    }
}
