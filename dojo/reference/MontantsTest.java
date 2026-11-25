package fr.dojo.virements;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MontantsTest extends ApiSupport {
    @Test
    void accepteUnCentime() throws Exception {
        var avant = etat();
        assertThat(effectuer("0.01").statusCode()).isEqualTo(201);
        verifierEcriture(avant, "0.01");
    }

    @ParameterizedTest(name = "{0} EUR est refusé")
    @ValueSource(strings = {"0.00", "-0.01"})
    void refuseUnMontantNonPositif(String montant) throws Exception {
        var avant = etat();
        var response = effectuer(montant);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).containsPattern("\"message\"\\s*:\\s*\"[^\"]+\"");
        assertThat(etat()).isEqualTo(avant);
    }
}
