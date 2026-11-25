package fr.dojo.virements.domaine.account.valueobject;

import fr.dojo.virements.domaine.account.exception.InvalidAccountException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountNumberTest {
    @Test
    void acceptsAndExposesAnAccountNumberUpTo64Characters() {
        assertThat(new AccountNumber("A".repeat(64)).value()).hasSize(64);
    }

    @Test
    void rejectsMissingOrBlankValue() {
        assertThatThrownBy(() -> new AccountNumber(null))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessage("The account number is required.")
                .extracting("code", "label")
                .containsExactly("err.fun.01", "Request rejected");
        assertThatThrownBy(() -> new AccountNumber("  "))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessage("The account number is required.");
    }

    @Test
    void rejectsSurroundingSpaces() {
        assertThatThrownBy(() -> new AccountNumber(" ACCOUNT-A"))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessage("The account number must not contain leading or trailing spaces.");
    }

    @Test
    void rejectsMoreThan64Characters() {
        assertThatThrownBy(() -> new AccountNumber("A".repeat(65)))
                .isInstanceOf(InvalidAccountException.class)
                .hasMessage("The account number cannot exceed 64 characters.");
    }
}
