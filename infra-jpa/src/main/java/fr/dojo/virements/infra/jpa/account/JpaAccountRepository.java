package fr.dojo.virements.infra.jpa.account;

import fr.dojo.virements.application.common.exception.TechnicalException;
import fr.dojo.virements.application.transfer.port.out.AccountRepository;
import fr.dojo.virements.domaine.account.Account;
import fr.dojo.virements.domaine.account.valueobject.AccountNumber;
import fr.dojo.virements.domaine.account.valueobject.Balance;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaAccountRepository implements AccountRepository {
    private final AccountJpaRepository repository;

    JpaAccountRepository(AccountJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Account> find(AccountNumber accountNumber) {
        try {
            return repository.findById(accountNumber.value())
                    .map(entity -> new Account(
                            new AccountNumber(entity.number()),
                            new Balance(entity.balance())));
        } catch (DataAccessException exception) {
            throw new TechnicalException("Unable to read account data.", exception);
        }
    }

    @Override
    public void save(Account account) {
        try {
            var entity = repository.findById(account.number().value())
                    .orElseGet(() -> new AccountEntity(
                            account.number().value(), account.balance().value()));
            entity.updateBalance(account.balance().value());
            repository.saveAndFlush(entity);
        } catch (DataAccessException exception) {
            throw new TechnicalException("Unable to save account data.", exception);
        }
    }
}
