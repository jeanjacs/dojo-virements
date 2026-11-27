# JDK 21 pour la collecte du dojo

Quand le participant demande « Prépare Java pour le dojo », configure le JDK
avant le premier message qui lance une tentative, par exemple `[dojo ex1 avant]`.

1. Cherche un JDK 21 installé. Sous Windows, consulte le `JAVA_HOME` utilisateur
   avec `[Environment]::GetEnvironmentVariable('JAVA_HOME', 'User')`, puis
   vérifie les dossiers d’installation usuels.
2. Exécute `java -version` avec le chemin absolu du candidat. Vérifie que la
   version majeure est 21 et que `javac` est présent dans le même dossier.
3. Dans `.bob/settings.json`, remplace uniquement le binaire des commandes
   de collecte de `UserPromptSubmit` et `PreToolUse`. Utilise le chemin absolu
   entre guillemets, échappe-le pour JSON et conserve les arguments
   `-jar dojo/target/dojo-tools.jar hook`.
4. Si aucun JDK 21 ne convient, demande son chemin avant toute modification.
   Laisse les variables d’environnement du système intactes.
5. Invite le participant à rouvrir le dossier dans Bob pour recharger
   les hooks, puis à lancer la tentative.
