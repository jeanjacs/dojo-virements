---
name: dojo-evaluation
description: Évalue une tentative du dojo après le marqueur [dojo fin] et présente le rapport.
---

Après clôture, utilise le numéro de tentative indiqué par le hook.
Relève dans la conversation et les sorties d’outils les scénarios exécutés
avant `[dojo fin]`. Compte chaque scénario distinct une fois. Appuie chaque
ligne sur une trace d’exécution. Si les traces sont indisponibles, indique
que les scénarios n’ont pas pu être relevés.

Écris les observations en UTF-8 dans `.dojo-runtime/observations-NUMERO.json`.
Utilise ce format, sans reprendre les résultats des contrôles de référence :

```json
{
  "cas_verifies": [
    {
      "cas": "Nom du scénario",
      "statut": "reussi",
      "resultat": "Résultat constaté",
      "preuve": "Test exécuté ou réponse HTTP observée"
    }
  ]
}
```

Utilise `echec` si le scénario a échoué. Si un même cas a été rejoué, retiens
son dernier résultat observé. Utilise `null` pour `cas_verifies` si les traces
sont indisponibles et `[]` si elles montrent qu’aucun cas n’a été exécuté.

Depuis la racine, exécute
`java -jar dojo/target/dojo-tools.jar evaluate NUMERO --observations .dojo-runtime/observations-NUMERO.json`.
Remplace `NUMERO` par le numéro annoncé aussi bien dans le chemin que dans la
commande. Appuie le rapport sur le JSON renvoyé, qui calcule le total.

Commence le rapport par un tableau avec une ligne par scénario, son résultat
observé (réussi ou en échec) et la preuve (test exécuté ou réponse obtenue).
Indique le total des scénarios distincts exécutés avant la clôture.
Présente ensuite le score global des contrôles de référence, la durée et
les appels observés, avec une ligne par nom d’outil fourni dans le JSON.
Si l’évaluation de référence est indisponible, indique la raison fournie.

Indique le chemin du rapport exporté que la commande affiche. La copie locale
est disponible dans `.dojo-runtime/rapport-NUMERO.json`.
