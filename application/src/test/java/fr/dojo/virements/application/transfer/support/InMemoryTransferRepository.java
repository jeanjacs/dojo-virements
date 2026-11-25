package fr.dojo.virements.application.transfer.support;

import fr.dojo.virements.application.transfer.port.out.TransferRepository;
import fr.dojo.virements.domaine.transfer.Transfer;

import java.util.ArrayList;
import java.util.List;

public final class InMemoryTransferRepository implements TransferRepository {
    private final List<Transfer> transfers = new ArrayList<>();

    @Override
    public void save(Transfer transfer) {
        transfers.add(transfer);
    }

    @Override
    public List<Transfer> findAll() {
        return List.copyOf(transfers);
    }
}
