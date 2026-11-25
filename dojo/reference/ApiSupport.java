package fr.dojo.virements;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

abstract class ApiSupport {
    private static final Pattern SOLDE = Pattern.compile("\"solde\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");
    @Value("${local.server.port}")
    int port;

    private final HttpClient client = HttpClient.newHttpClient();

    HttpResponse<String> effectuer(String montant) throws Exception {
        return post("/virements", "application/json", """
                {"source":"COMPTE-A","destination":"COMPTE-B","montant":%s}
                """.formatted(montant));
    }

    HttpResponse<String> importer(String montant) throws Exception {
        return post("/virements/import", "text/csv", """
                source,destination,montant
                COMPTE-A,COMPTE-B,%s
                """.formatted(montant));
    }

    Etat etat() throws Exception {
        return new Etat(solde("COMPTE-A"), solde("COMPTE-B"), get("/virements"));
    }

    void verifierEcriture(Etat avant, String montant) throws Exception {
        var apres = etat();
        var valeur = new BigDecimal(montant);
        assertThat(apres.source()).isEqualByComparingTo(avant.source().subtract(valeur));
        assertThat(apres.destination()).isEqualByComparingTo(avant.destination().add(valeur));
        assertThat(nombreVirements(apres.historique())).isEqualTo(nombreVirements(avant.historique()) + 1);
    }

    private long nombreVirements(String historique) {
        return Pattern.compile("\"id\"\\s*:").matcher(historique).results().count();
    }

    private BigDecimal solde(String compte) throws Exception {
        var valeur = SOLDE.matcher(get("/comptes/" + compte));
        assertThat(valeur.find()).isTrue();
        return new BigDecimal(valeur.group(1));
    }

    private String get(String path) throws Exception {
        var response = client.send(request(path).GET().build(), HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        return response.body();
    }

    private HttpResponse<String> post(String path, String type, String body) throws Exception {
        return client.send(request(path).header("Content-Type", type)
                .POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
    }

    record Etat(BigDecimal source, BigDecimal destination, String historique) {}
}
