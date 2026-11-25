package fr.dojo.virements.domaine.account;

import fr.dojo.virements.domaine.account.valueobject.AccountNumber;
import fr.dojo.virements.domaine.account.valueobject.Balance;
import fr.dojo.virements.domaine.common.AggregateRoot;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {
    @Test
    void exposesItsTypedNumberAsAggregateIdentity() {
        var number = new AccountNumber("ACCOUNT-A");
        AggregateRoot<AccountNumber> aggregate = new Account(number, new Balance(new BigDecimal("100.00")));

        assertThat(aggregate.id()).isEqualTo(number);
    }

    @Test
    void delegatesDebitAndCreditWithoutMutatingTheAggregate() {
        var account = account("100.00");

        var debited = account.debit(new BigDecimal("25.50"));
        var credited = debited.credit(new BigDecimal("10.25"));

        assertThat(account.balance().value()).isEqualByComparingTo("100.00");
        assertThat(debited.balance().value()).isEqualByComparingTo("74.50");
        assertThat(credited.balance().value()).isEqualByComparingTo("84.75");
    }

    @Test
    void requiresTypedNumberAndBalance() {
        assertThatThrownBy(() -> new Account(null, new Balance(BigDecimal.ZERO)))
                .hasMessage("The account number is required.");
        assertThatThrownBy(() -> new Account(new AccountNumber("ACCOUNT-A"), null))
                .hasMessage("The account balance is required.");
    }

    private static Account account(String balance) {
        return new Account(new AccountNumber("ACCOUNT-A"), new Balance(new BigDecimal(balance)));
    }
}
