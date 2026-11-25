package fr.dojo.virements.infra.jpa.transfer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "virement")
class TransferEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ordre")
    private Long sequence;

    @Column(nullable = false, unique = true)
    private UUID id;

    @Column(length = 64, nullable = false)
    private String source;

    @Column(length = 64, nullable = false)
    private String destination;

    @Column(name = "montant", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    protected TransferEntity() {
    }

    TransferEntity(UUID id, String source, String destination, BigDecimal amount) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.amount = amount;
    }

    UUID id() {
        return id;
    }

    String source() {
        return source;
    }

    String destination() {
        return destination;
    }

    BigDecimal amount() {
        return amount;
    }
}
