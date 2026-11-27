package fr.dojo.virements.application.transfer.support;

import fr.dojo.virements.application.transfer.port.out.TransferRepository;
import fr.dojo.virements.domaine.transfer.Transfer;
import fr.dojo.virements.domaine.transfer.valueobject.TransferId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class InMemoryTransferRepository implements TransferRepository {
    private final List<Transfer> transfers = new ArrayList<>();

    @Override
    public void save(Transfer transfer) {
        transfers.add(transfer);
    }

    @Override
    public Optional<Transfer> find(TransferId id) {
        return transfers.stream().filter(transfer -> transfer.id().equals(id)).findFirst();
    }

    @Override
    public List<Transfer> findAll() {
        return List.copyOf(transfers);
    }
}
