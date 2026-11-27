package fr.dojo.virements.application.transfer.port.out;

import fr.dojo.virements.domaine.transfer.Transfer;
import fr.dojo.virements.domaine.transfer.valueobject.TransferId;

import java.util.List;
import java.util.Optional;

public interface TransferRepository {
    void save(Transfer transfer);

    Optional<Transfer> find(TransferId id);

    List<Transfer> findAll();
}
