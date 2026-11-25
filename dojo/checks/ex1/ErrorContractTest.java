package fr.dojo.virements;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class ErrorContractTest {
    private static final String TRANSFERS = "/api/v1/transfers";
    private static final String ACCOUNTS = "/api/v1/accounts/{accountNumber}";
    private static final String SOURCE = "COMPTE-A";
    private static final String DESTINATION = "COMPTE-B";

    @Autowired
    RestTestClient client;

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-0.01"})
    void refusesAnInvalidAmountWithACompatibleError(String value) {
        var before = state();
        var error = submit(new BigDecimal(value)).expectStatus().isBadRequest()
                .expectBody(ErrorSnapshot.class).returnResult().getResponseBody();

        assertEquals(before, state(), "A refused transfer must preserve both balances and the history.");
        assertNotNull(error);
        assertEquals("err.fun.02", error.code());
        assertEquals("The amount must be strictly positive.", error.message());
        assertEquals("Invalid transfer. The amount must be strictly positive.", error.description());
    }

    @Test
    void stillRecordsATransferOfOneCent() {
        var before = state();
        var cent = new BigDecimal("0.01");
        var transfer = submit(cent).expectStatus().isCreated()
                .expectBody(TransferSnapshot.class).returnResult().getResponseBody();
        var after = state();

        assertNotNull(transfer);
        assertNotNull(transfer.id());
        assertEquals(SOURCE, transfer.source());
        assertEquals(DESTINATION, transfer.destination());
        assertEquals(0, cent.compareTo(transfer.amount()));
        assertEquals(0, before.source().subtract(cent).compareTo(after.source()));
        assertEquals(0, before.destination().add(cent).compareTo(after.destination()));
        assertEquals(before.history().size() + 1, after.history().size());
        assertTrue(after.history().containsAll(before.history()));
        assertTrue(after.history().contains(transfer));
    }

    private RestTestClient.ResponseSpec submit(BigDecimal amount) {
        return client.post().uri(TRANSFERS).contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("source", SOURCE, "destination", DESTINATION, "amount", amount)).exchange();
    }

    private State state() {
        var history = client.get().uri(TRANSFERS).exchange().expectStatus().isOk()
                .expectBody(TransferSnapshot[].class).returnResult().getResponseBody();
        assertNotNull(history);
        return new State(balance(SOURCE), balance(DESTINATION), List.of(history));
    }

    private BigDecimal balance(String account) {
        var result = client.get().uri(ACCOUNTS, account).exchange().expectStatus().isOk()
                .expectBody(AccountSnapshot.class).returnResult().getResponseBody();
        assertNotNull(result);
        return result.balance();
    }

    record ErrorSnapshot(String code, String message, String description) {
    }

    record TransferSnapshot(UUID id, String source, String destination, BigDecimal amount) {
    }

    record AccountSnapshot(String account, BigDecimal balance) {
    }

    record State(BigDecimal source, BigDecimal destination, List<TransferSnapshot> history) {
    }
}
