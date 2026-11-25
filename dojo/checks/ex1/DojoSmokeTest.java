package fr.dojo.virements;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class DojoSmokeTest {
    @Autowired
    RestTestClient client;

    @Test
    void servesInitializedAccountData() {
        client.get().uri("/api/v1/accounts/COMPTE-A").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.account").isEqualTo("COMPTE-A")
                .jsonPath("$.balance").isEqualTo(5000);
    }
}
