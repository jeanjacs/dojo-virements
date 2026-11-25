package fr.dojo.virements.domaine.account.valueobject;

import fr.dojo.virements.domaine.account.exception.InsufficientFundsException;
import fr.dojo.virements.domaine.account.exception.InvalidAccountException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BalanceTest {
    @Test
    void normalizesValueToCents() {
        var balance = new Balance(new BigDecimal("10.5"));

        assertThat(balance.value()).isEqualByComparingTo("10.50");
        assertThat(balance.value().scale()).isEqualTo(2);
    }

    @Test
    void debitsAndCreditsWithoutMutatingTheOriginalValue() {
        var balance = new Balance(new BigDecimal("100.00"));

        var debited = balance.debit(new BigDecimal("25.50"));
        var credited = debited.credit(new BigDecimal("10.25"));

        assertThat(balance.value()).isEqualByComparingTo("100.00");
        assertThat(debited.value()).isEqualByComparingTo("74.50");
        assertThat(credited.value()).isEqualByComparingTo("84.75");
    }

    @Test
    void rejectsMissingSubCentOrNegativeBalance() {
        assertThatThrownBy(() -> new Balance(null))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessage("The account balance is required.");
        assertThatThrownBy(() -> new Balance(new BigDecimal("10.001")))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessage("The account balance must be expressed in cents.");
        assertThatThrownBy(() -> new Balance(new BigDecimal("-0.01")))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessage("The account balance cannot be negative.");
    }

    @Test
    void rejectsInvalidOperationAmounts() {
        var balance = new Balance(new BigDecimal("100.00"));

        assertThatThrownBy(() -> balance.debit(null))
                .hasMessage("The account operation amount is required.");
        assertThatThrownBy(() -> balance.credit(BigDecimal.ZERO))
                .hasMessage("The account operation amount must be strictly positive.");
        assertThatThrownBy(() -> balance.debit(new BigDecimal("-1.00")))
                .hasMessage("The account operation amount must be strictly positive.");
        assertThatThrownBy(() -> balance.credit(new BigDecimal("1.001")))
                .hasMessage("The account operation amount must be expressed in cents.");
    }

    @Test
    void rejectsDebitWhenFundsAreInsufficient() {
        assertThatThrownBy(() -> new Balance(new BigDecimal("10.00"))
                .debit(new BigDecimal("10.01")))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessage("The account balance is insufficient.")
                .extracting("code", "label")
                .containsExactly("err.fun.03", "Request rejected");
    }
}
