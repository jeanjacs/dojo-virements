package fr.dojo.tools;

import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

record CaseObservations(List<ObservedCase> cases) {
    record ObservedCase(String cas, String statut, String resultat, String preuve) {
    }

    static CaseObservations read(Path path) throws IOException {
        var input = Dojo.JSON.readTree(path.toFile());
        if (input == null || !input.isObject() || !input.has("cas_verifies")) {
            throw new IOException("Attendu : un objet JSON avec cas_verifies (liste ou null).");
        }
        var cases = input.get("cas_verifies");
        if (cases.isNull()) {
            return new CaseObservations(null);
        }
        if (!cases.isArray()) {
            throw new IOException("cas_verifies doit être une liste ou null.");
        }
        var distinct = new LinkedHashMap<String, ObservedCase>();
        for (var item : cases) {
            if (!item.isObject()) {
                throw new IOException("Chaque cas vérifié doit être un objet.");
            }
            var name = requiredText(item, "cas");
            var status = requiredText(item, "statut");
            if (!Set.of("reussi", "echec").contains(status)) {
                throw new IOException("Statut du cas attendu : reussi ou echec.");
            }
            var observed = new ObservedCase(name, status, requiredText(item, "resultat"), requiredText(item, "preuve"));
            distinct.put(name.toLowerCase(Locale.ROOT).replaceAll("\\s+", " "), observed);
        }
        return new CaseObservations(List.copyOf(distinct.values()));
    }

    private static String requiredText(JsonNode item, String field) throws IOException {
        var value = item.path(field);
        if (!value.isString() || value.asString().isBlank()) {
            throw new IOException("Champ texte obligatoire pour un cas vérifié : " + field + ".");
        }
        return value.asString().strip();
    }
}
