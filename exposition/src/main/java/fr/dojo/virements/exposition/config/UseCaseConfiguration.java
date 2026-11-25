package fr.dojo.virements.exposition.config;

import fr.dojo.virements.application.transfer.TransferService;
import fr.dojo.virements.application.transfer.port.out.AccountRepository;
import fr.dojo.virements.application.transfer.port.out.TransferRepository;
import fr.dojo.virements.application.transfer.port.out.TransferUnitOfWork;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UseCaseConfiguration {
    @Bean
    TransferService transferService(AccountRepository accounts, TransferRepository transfers,
                                    TransferUnitOfWork unitOfWork) {
        return new TransferService(accounts, transfers, unitOfWork);
    }
}
