package fr.dojo.virements.application.transfer.port.out;

import fr.dojo.virements.domaine.transfer.Transfer;

import java.util.List;

public interface TransferRepository {
    void save(Transfer transfer);

    List<Transfer> findAll();
}
