package fr.dojo.virements.infra.jpa.transfer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface TransferJpaRepository extends JpaRepository<TransferEntity, Long> {
    List<TransferEntity> findAllByOrderBySequenceAsc();
}
