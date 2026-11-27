package fr.dojo.virements.infra.jpa.transfer;

import fr.dojo.virements.application.common.exception.TechnicalException;
import fr.dojo.virements.application.transfer.port.out.TransferRepository;
import fr.dojo.virements.domaine.account.valueobject.AccountNumber;
import fr.dojo.virements.domaine.transfer.Transfer;
import fr.dojo.virements.domaine.transfer.valueobject.TransferAmount;
import fr.dojo.virements.domaine.transfer.valueobject.TransferId;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaTransferRepository implements TransferRepository {
    private final TransferJpaRepository repository;

    JpaTransferRepository(TransferJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Transfer transfer) {
        try {
            repository.saveAndFlush(new TransferEntity(
                    transfer.id().value(),
                    transfer.source().value(),
                    transfer.destination().value(),
                    transfer.amount().value()));
        } catch (DataAccessException exception) {
            throw new TechnicalException("Unable to save transfer data.", exception);
        }
    }

    @Override
    public Optional<Transfer> find(TransferId id) {
        try {
            return repository.findTransferById(id.value()).map(JpaTransferRepository::toDomain);
        } catch (DataAccessException exception) {
            throw new TechnicalException("Unable to read transfer data.", exception);
        }
    }

    @Override
    public List<Transfer> findAll() {
        try {
            return repository.findAllByOrderBySequenceAsc().stream()
                    .map(JpaTransferRepository::toDomain)
                    .toList();
        } catch (DataAccessException exception) {
            throw new TechnicalException("Unable to read transfer data.", exception);
        }
    }

    private static Transfer toDomain(TransferEntity entity) {
        return new Transfer(new TransferId(entity.id()), new AccountNumber(entity.source()),
                new AccountNumber(entity.destination()), new TransferAmount(entity.amount()));
    }
}
