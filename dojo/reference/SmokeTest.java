package fr.dojo.virements;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SmokeTest extends ApiSupport {
    @Test
    void virementDisponible() throws Exception {
        var avant = etat();
        assertThat(effectuer("25.37").statusCode()).isEqualTo(201);
        verifierEcriture(avant, "25.37");
    }
}
