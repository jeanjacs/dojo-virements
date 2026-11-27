package fr.dojo.virements.exposition.api;

import fr.dojo.virements.exposition.api.model.TransferRequest;
import fr.dojo.virements.exposition.api.model.TransferResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ApiEndpointsIntegrationTest {
    @Autowired
    RestTestClient client;

    @Test
    void redirectsRootToSwagger() {
        client.get().uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(HttpHeaders.LOCATION, ".*/swagger-ui/index\\.html");

        client.get().uri("/swagger-ui/index.html")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void exposesH2Console() {
        client.get().uri("/h2-console/")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void exposesOpenApiContract() {
        client.get().uri("/v3/api-docs")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.info.title").isEqualTo("Transfers API")
                .jsonPath("$['paths']['/api/v1/transfers']").exists()
                .jsonPath("$['paths']['/api/v1/transfers/{id}'].get.responses['404']").exists()
                .jsonPath("$['paths']['/api/v1/accounts/{accountNumber}']").exists()
                .jsonPath("$.components.schemas.ApiError.properties.message").exists();
    }

    @Test
    void loadsAccountsIntoH2() {
        client.get().uri("/api/v1/accounts/COMPTE-A")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.account").isEqualTo("COMPTE-A")
                .jsonPath("$.balance").isEqualTo(5000.00);
    }

    @Test
    void createsTransferAndUpdatesAccountsAndHistory() {
        client.post().uri("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new TransferRequest("COMPTE-A", "COMPTE-B", new BigDecimal("25.00")))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.source").isEqualTo("COMPTE-A")
                .jsonPath("$.destination").isEqualTo("COMPTE-B")
                .jsonPath("$.amount").isEqualTo(25.00);

        client.get().uri("/api/v1/accounts/COMPTE-A")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(4975.00);

        client.get().uri("/api/v1/accounts/COMPTE-B")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(175.00);

        client.get().uri("/api/v1/transfers")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].amount").isEqualTo(25.00);
    }

    @Test
    void findsAnExistingTransferByIdentifier() {
        var transfer = client.post().uri("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new TransferRequest("COMPTE-A", "COMPTE-B", new BigDecimal("25.00")))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransferResponse.class)
                .returnResult().getResponseBody();
        assertNotNull(transfer);

        var history = client.get().uri("/api/v1/transfers")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransferResponse[].class)
                .returnResult().getResponseBody();
        assertNotNull(history);
        var historyEntry = Arrays.stream(history).filter(item -> item.id().equals(transfer.id())).findFirst().orElseThrow();

        var detail = client.get().uri("/api/v1/transfers/{id}", transfer.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransferResponse.class)
                .returnResult().getResponseBody();
        assertEquals(historyEntry, detail);
    }

    @Test
    void returnsTheCompatibleErrorContractWhenATransferIsMissing() {
        var missing = UUID.randomUUID();
        var message = "Transfer not found: " + missing;

        client.get().uri("/api/v1/transfers/{id}", missing)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("err.fun.05")
                .jsonPath("$.message").isEqualTo(message)
                .jsonPath("$.description").isEqualTo("Transfer lookup failed. " + message);
    }

    @Test
    void mapsBusinessExceptionsToBadRequest() {
        client.post().uri("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new TransferRequest("UNKNOWN", "COMPTE-B", new BigDecimal("25.00")))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("err.fun.04")
                .jsonPath("$.message").isEqualTo("Account not found: UNKNOWN")
                .jsonPath("$.description").isEqualTo("Request rejected. Account not found: UNKNOWN");
    }
}
