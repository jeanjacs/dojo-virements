# Exercice 3 · Diagnostiquer un test rouge avec un skill

Le fichier `dojo/ex3/FixtureReader.java` est le programme à corriger.
Le test de référence dans `dojo/checks/ex3/` reproduit un échec en changeant
le répertoire de lancement. La commande de contrôle est
`java -jar dojo/target/dojo-tools.jar check-ex3`.

Les ressources à réaliser avec Bob sont le hook de protection des tests
et le skill de diagnostic. Le squelette Java du hook est
`dojo/ex3/ReferenceGuard.java`. Ses contrôles sont dans `dojo/checks/ex3-hook/`
et s'exécutent avec `java -jar dojo/target/dojo-tools.jar check-ex3-hook`.
Les modèles neutres se trouvent dans `docs/modeles/`. Le pas-à-pas sera
fourni séparément.
