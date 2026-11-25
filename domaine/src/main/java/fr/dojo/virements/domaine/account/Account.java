package fr.dojo.virements.domaine.account;

import fr.dojo.virements.domaine.account.exception.InvalidAccountException;
import fr.dojo.virements.domaine.account.valueobject.AccountNumber;
import fr.dojo.virements.domaine.account.valueobject.Balance;
import fr.dojo.virements.domaine.common.AggregateRoot;

import java.math.BigDecimal;

public record Account(AccountNumber number, Balance balance) implements AggregateRoot<AccountNumber> {
    public Account {
        if (number == null) {
            throw new InvalidAccountException("The account number is required.");
        }
        if (balance == null) {
            throw new InvalidAccountException("The account balance is required.");
        }
    }

    public Account debit(BigDecimal amount) {
        return new Account(number, balance.debit(amount));
    }

    public Account credit(BigDecimal amount) {
        return new Account(number, balance.credit(amount));
    }

    @Override
    public AccountNumber id() {
        return number;
    }
}
