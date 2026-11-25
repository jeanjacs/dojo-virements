# Exercice 2 · Observer la consultation d'un ADR

Le chantier ajoute `GET /api/v1/transfers/{id}` pour consulter un virement.
Le résultat reprend les champs de l'historique. Un UUID valide mais inconnu
donne HTTP 404. Les contrôles sont dans `dojo/checks/ex2/`.

Les ressources à créer avec Bob sont une rule de nommage Java en anglais
commune aux deux copies et un ADR de modularisation pour la copie après.
Les modèles neutres se trouvent dans `docs/modeles/`. Le pas-à-pas sera
fourni séparément.
