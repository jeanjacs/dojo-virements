package fr.dojo.virements.exposition.common.error;

import com.jayway.jsonpath.JsonPath;
import fr.dojo.virements.application.common.exception.TechnicalException;
import fr.dojo.virements.application.transfer.TransferService;
import fr.dojo.virements.application.transfer.port.out.TransferRepository;
import fr.dojo.virements.domaine.transfer.Transfer;
import fr.dojo.virements.domaine.transfer.valueobject.TransferId;
import fr.dojo.virements.exposition.api.TransferController;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransferLookupErrorTest {
    private final TransferRepository unavailableTransfers = new TransferRepository() {
        @Override
        public Optional<Transfer> find(TransferId id) {
            throw new TechnicalException("Unable to read transfer data.",
                    new IllegalStateException("Internal database failure"));
        }

        @Override
        public void save(Transfer transfer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Transfer> findAll() {
            throw new UnsupportedOperationException();
        }
    };

    private final TransferController controller = new TransferController(
            new TransferService(null, unavailableTransfers, null));

    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(controller)
            .addPlaceholderValue("api.base-path", "/api/v1")
            .setControllerAdvice(new ApiExceptionHandler())
            .build();

    @Test
    void rejectsMalformedIdentifiersBeforeLookingUpTheTransfer() throws Exception {
        mvc.perform(get("/api/v1/transfers/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsAGenericErrorWhenTheLookupFails() throws Exception {
        var response = mvc.perform(get("/api/v1/transfers/{id}", UUID.randomUUID()))
                .andExpect(status().isInternalServerError())
                .andReturn().getResponse();
        var error = JsonPath.parse(response.getContentAsString());
        assertEquals("err.tech.01", error.read("$.code"));
        assertEquals("An unexpected technical error occurred.", error.read("$.message"));
        assertEquals("Request could not be completed. An unexpected technical error occurred.",
                error.read("$.description"));
    }
}
