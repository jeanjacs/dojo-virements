# Exercice 1 · Cadrer une évolution du contrat d'erreur

Le chantier ajoute le champ `description` au contrat d'erreur de l'API de
virement. Les valeurs de `code` et `message` restent stables. La description
présente le type d'erreur en toutes lettres, puis reprend le message.
Pour un montant nul ou négatif, elle vaut
`Invalid transfer. The amount must be strictly positive.`

Les contrôles de référence dans `dojo/checks/ex1/` vérifient aussi la conservation
des soldes et de l'historique en cas de refus, et l'acceptation d'un virement
d'un centime.

Le livrable de préparation est une demande coordonnée avec Bob. Les deux
passages comparent un cadrage court et un cadrage avec des critères
d'acceptation. Le pas-à-pas sera fourni séparément.
