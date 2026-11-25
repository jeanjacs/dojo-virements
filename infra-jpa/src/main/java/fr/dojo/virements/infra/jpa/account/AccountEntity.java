package fr.dojo.virements.infra.jpa.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;

@Entity
@Table(name = "compte")
class AccountEntity {
    @Id
    @Column(name = "numero", length = 64, nullable = false)
    private String number;

    @Column(name = "solde", precision = 19, scale = 2, nullable = false)
    private BigDecimal balance;

    @Version
    private long version;

    protected AccountEntity() {
    }

    AccountEntity(String number, BigDecimal balance) {
        this.number = number;
        this.balance = balance;
    }

    String number() {
        return number;
    }

    BigDecimal balance() {
        return balance;
    }

    void updateBalance(BigDecimal newBalance) {
        balance = newBalance;
    }
}
