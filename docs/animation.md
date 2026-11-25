# Animer le dojo IBM Bob

| Temps | Séquence |
| --- | --- |
| 5 min | Ouverture et objectifs |
| 10 min | Ressources, des rules aux ADR, puis skill et hook |
| 10 min | Vérification dans Bob |
| 25 min | Cadrer une demande |
| 25 min | Observer si Bob trouve et utilise un ADR |
| 25 min | Diagnostiquer un test rouge et démontrer le hook de protection |
| 20 min | Restitution et action commune |

Le pilote d'un binôme reste le même pendant les deux tentatives et peut changer
entre les exercices. Fixez le modèle et les permissions. Les copies séparées
font repartir les deux passages du même socle. Le hook consigne uniquement des
métadonnées d'appel avant exécution. Les relances correctives et le placement des
responsabilités se vérifient en relisant le chat et le code.

Les rapports comparent d'abord les contrôles et le résultat architectural,
puis la durée et les appels. Les appels constituent une mesure d'activité.
Relevez les Bobcoins depuis Bob uniquement si vous pouvez isoler la tentative.
Le second passage bénéficie de l'apprentissage du binôme.

## Évaluer les deux passages

Pour chaque exercice, créez les deux copies à partir du même commit de départ
de la branche `exercises/part-N`. Ouvrez une nouvelle conversation pour
chaque passage et clôturez chacun avec le skill `dojo-evaluation`.

L'évaluateur applique les mêmes contrôles de référence aux deux versions.
Si le passage avant réussit, conservez ce résultat et relevez ce qui a été
observé dans les deux passages. Une réussite identique est un résultat valide.

| Exercice | Contrôles communs | Preuves à comparer |
| --- | --- | --- |
| 1 | Les refus conservent `code` et `message`, ajoutent une `description` qui associe le libellé au message et préservent l'état. Un centime reste accepté. | Déroulé du cadrage, compatibilité dans le diff et éventuelles relances. |
| 2 | La consultation couvre un virement existant et un identifiant inconnu. | Lecture de l'ADR dans la trace Bob, rôle des modules dans le diff et noms introduits. |
| 3 | Le lecteur trouve le bon fichier depuis les deux répertoires de lancement. | Reproduction du test rouge, diagnostic justifié, correction et résultat de la relance. |

La durée et le nombre d'appels aident à décrire le déroulement. Une différence
sur un binôme reste une observation de l'atelier, avec un possible effet d'apprentissage.

Le projet est une simulation bancaire avec deux comptes fictifs et sans frontend.
Pour l'exercice 2, les développeurs créent la rule de nommage Java en anglais
pour les deux copies et rédigent l'ADR de modularisation dans la seconde.

Pour l'exercice 3, les deux copies partent du même test rouge et du même
squelette de hook. Les développeurs construisent le hook et créent le skill
de diagnostic à utiliser dans la seconde copie.

Les pas-à-pas de création des ressources sont distribués séparément.

En restitution, demander à chaque binôme ce qui a changé et quelle preuve le montre.
On peut relever une lecture de l'ADR avant l'édition, une consultation qui
passe par un port applicatif et deux contrôles HTTP réussis. La lecture,
le diff et le rapport donnent chacun une preuve différente.
Relever aussi les résultats identiques et les limites de la comparaison.

Choisir une action commune sur le dépôt. On peut par exemple comparer les
consultations réalisées et préciser la responsabilité d'un module qui a
suscité des choix différents.
