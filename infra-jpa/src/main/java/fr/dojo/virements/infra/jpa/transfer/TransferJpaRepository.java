package fr.dojo.virements.infra.jpa.transfer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface TransferJpaRepository extends JpaRepository<TransferEntity, Long> {
    Optional<TransferEntity> findTransferById(UUID id);

    List<TransferEntity> findAllByOrderBySequenceAsc();
}
