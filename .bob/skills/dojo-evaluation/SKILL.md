---
name: dojo-evaluation
description: Évalue une tentative du dojo après le marqueur [dojo fin] et présente le rapport.
---

Après clôture, utilise le numéro de tentative indiqué par le hook.
Exécute `java -jar dojo/target/dojo-tools.jar evaluate NUMERO` depuis la racine.
Présente la durée, les appels observés et les contrôles réussis ou en échec.
Si la mesure est indisponible, restitue la raison fournie par l'évaluateur.
Le rapport est disponible dans `.dojo-runtime/rapport-NUMERO.json`.
