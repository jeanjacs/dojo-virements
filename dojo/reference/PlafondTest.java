package fr.dojo.virements;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PlafondTest extends ApiSupport {
    @ParameterizedTest(name = "API accepte {0} EUR")
    @ValueSource(strings = {"1.00", "1000.00"})
    void apiAccepte(String montant) throws Exception {
        var avant = etat();
        assertThat(effectuer(montant).statusCode()).isEqualTo(201);
        verifierEcriture(avant, montant);
    }

    @ParameterizedTest(name = "API refuse {0} EUR")
    @ValueSource(strings = {"1000.01", "1200.00"})
    void apiRefuse(String montant) throws Exception {
        var avant = etat();
        var response = effectuer(montant);
        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.body()).containsPattern("\"message\"\\s*:\\s*\"[^\"]+\"");
        assertThat(etat()).isEqualTo(avant);
    }

    @ParameterizedTest(name = "Import accepte {0} EUR")
    @ValueSource(strings = {"1.00", "1000.00"})
    void importAccepte(String montant) throws Exception {
        var avant = etat();
        var response = importer(montant);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).containsPattern("\"importes\"\\s*:\\s*1");
        verifierEcriture(avant, montant);
    }

    @ParameterizedTest(name = "Import refuse {0} EUR")
    @ValueSource(strings = {"1000.01", "1200.00"})
    void importRefuse(String montant) throws Exception {
        var avant = etat();
        var response = importer(montant);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).containsPattern("\"importes\"\\s*:\\s*0")
                .containsPattern("\"ligne\"\\s*:\\s*2");
        assertThat(etat()).isEqualTo(avant);
    }
}
