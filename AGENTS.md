# Dojo Virements

Simulation bancaire locale en euros avec Spring Boot 4, Java 21, Maven et H2.

- Respecte le périmètre de la fiche de l’exercice.
- Utilise `BigDecimal` pour les montants. Un virement refusé laisse les soldes et l’historique inchangés.
- Exécute les tests du projet avec `mvn test` ou la commande ciblée indiquée dans la fiche.
- Préserve les contrôles de référence dans `dojo/reference/` et `dojo/checks/`, l’évaluateur dans `dojo/src/main/` et le hook de collecte.
- Après `[dojo fin]`, utilise le numéro de tentative indiqué par le hook avec le skill `dojo-evaluation`. Présente le résultat des contrôles et le chemin du rapport.
