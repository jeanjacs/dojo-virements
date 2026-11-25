package fr.dojo.virements;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class TransferLookupTest {
    private static final String TRANSFERS = "/api/v1/transfers";
    private static final String LOOKUP = TRANSFERS + "/{id}";

    @Autowired
    RestTestClient client;

    @Test
    void returnsTheFieldsOfAnExistingTransfer() {
        var transfer = createTransfer();

        var found = client.get().uri(LOOKUP, transfer.id()).exchange()
                .expectStatus().isOk()
                .expectBody(TransferSnapshot.class)
                .returnResult().getResponseBody();

        assertNotNull(found);
        assertEquals(transfer.id(), found.id());
        assertEquals(transfer.source(), found.source());
        assertEquals(transfer.destination(), found.destination());
        assertNotNull(found.amount());
        assertEquals(0, transfer.amount().compareTo(found.amount()));
    }

    @Test
    void distinguishesAnUnknownIdentifierFromAnExistingOne() {
        var transfer = createTransfer();

        client.get().uri(LOOKUP, transfer.id()).exchange().expectStatus().isOk();
        client.get().uri(LOOKUP, new UUID(0, 0)).exchange().expectStatus().isNotFound();
    }

    private TransferSnapshot createTransfer() {
        var body = Map.of("source", "COMPTE-A", "destination", "COMPTE-B",
                "amount", new BigDecimal("1.25"));
        var created = client.post().uri(TRANSFERS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransferSnapshot.class)
                .returnResult().getResponseBody();
        assertNotNull(created);
        assertNotNull(created.id());
        assertNotNull(created.amount());
        return created;
    }

    record TransferSnapshot(UUID id, String source, String destination, BigDecimal amount) {
    }
}
