# Exercice 1 · Cadrer un prompt pour guider Bob

**ℹ️ Information** · Bob ajoute `description` aux réponses de refus. L’exercice compare les vérifications obtenues avec une consigne courte et une consigne détaillée.

**ℹ️ À savoir · Prompt** · Un prompt est le message qui confie une tâche à Bob. Il peut préciser le contexte, le périmètre et les résultats attendus. Ici, la consigne courte demande d’ajouter `description` à la réponse d’erreur. La consigne détaillée précise aussi les cas à tester et les résultats à vérifier.

## Avant de commencer · Modes de réalisation

| Votre situation | Parcours |
| --- | --- |
| L’animateur vous a affecté au groupe 1 | [Mode A · Groupe 1, consigne courte](#groupe-1--consigne-courte) |
| L’animateur vous a affecté au groupe 2 | [Mode A · Groupe 2, consigne détaillée](#groupe-2--consigne-détaillée) |
| Vous réalisez l’exercice seul | [Mode B · Deux tests en autonomie](#mode-b--deux-tests-en-autonomie) |

**ℹ️ Information · Prérequis** · Git, JDK 21, Maven et IBM Bob IDE. Les hooks du projet doivent être autorisés dans Bob. Dans le terminal de Bob, `java -version` et `mvn -v` doivent afficher Java 21.

## Mode A · Un test en groupe

### Groupe 1 · Consigne courte

#### 1. Préparer le projet

**▶ Action** · Dans le dossier du projet Virements qui contient le `pom.xml` parent, lancez `git checkout exercises/part-1` si la branche active est différente. Puis lancez le build.

```sh
mvn clean install
```

**▶ Action** · Vérifiez que Maven affiche `BUILD SUCCESS` et que `dojo/target/dojo-tools.jar` existe.

#### 2. Envoyer votre consigne

**▶ Action** · Dans Bob, ouvrez le dossier du projet Virements qui contient le `pom.xml` parent. Dans une nouvelle conversation, envoyez ce bloc en un seul message.

```text
[dojo ex1 avant]
Ajoute un champ description à la réponse de l’API quand une demande de virement est refusée. Vérifie le résultat.
```

#### 3. Évaluer et partager le résultat

**▶ Action** · Après la réponse finale de Bob, envoyez ce message dans la même conversation.

```text
[dojo fin] Évalue cette tentative avec le skill dojo-evaluation.
```

**▶ Action** · Ouvrez le rapport JSON au chemin affiché par Bob. Gardez-le pour le débrief.

**↪ Action conditionnelle** · Si Bob affiche un numéro de tentative sans générer le rapport, exécutez la commande suivante depuis le dossier ouvert dans Bob. Remplacez `NUMERO` par le numéro affiché.

```sh
java -jar dojo/target/dojo-tools.jar evaluate NUMERO
```

**▶ Action** · Conservez votre rapport pour les échanges en fin de séance.

### Groupe 2 · Consigne détaillée

#### 1. Préparer le projet

**▶ Action** · Dans le dossier du projet Virements qui contient le `pom.xml` parent, lancez `git checkout exercises/part-1` si la branche active est différente. Puis lancez le build.

```sh
mvn clean install
```

**▶ Action** · Vérifiez que Maven affiche `BUILD SUCCESS` et que `dojo/target/dojo-tools.jar` existe.

#### 2. Envoyer votre consigne

**▶ Action** · Dans Bob, ouvrez le dossier du projet Virements qui contient le `pom.xml` parent. Dans une nouvelle conversation, envoyez ce bloc en un seul message.

```text
[dojo ex1 apres]
Ajoute description aux refus de POST /api/v1/transfers. Conserve code et message. Compose description avec le libellé de l’erreur, un point, une espace et le message.

Vérifie 0 € et −0,01 €. Attends HTTP 400, le code err.fun.02, le message « The amount must be strictly positive. » et la description « Invalid transfer. The amount must be strictly positive. ». Après chaque refus, vérifie que les deux soldes et l’historique restent identiques.

Vérifie 0,01 € avec un solde suffisant. Attends HTTP 201, le débit, le crédit et un virement ajouté à l’historique.
```

#### 3. Évaluer et partager le résultat

**▶ Action** · Après la réponse finale de Bob, envoyez ce message dans la même conversation.

```text
[dojo fin] Évalue cette tentative avec le skill dojo-evaluation.
```

**▶ Action** · Ouvrez le rapport JSON au chemin affiché par Bob. Gardez-le pour le débrief.

**↪ Action conditionnelle** · Si Bob affiche un numéro de tentative sans générer le rapport, exécutez la commande suivante depuis le dossier ouvert dans Bob. Remplacez `NUMERO` par le numéro affiché.

```sh
java -jar dojo/target/dojo-tools.jar evaluate NUMERO
```

**▶ Action** · Conservez votre rapport pour les échanges en fin de séance.

## Mode B · Deux tests en autonomie

### B1. Générer les deux dossiers

**▶ Action** · Dans le dossier du projet Virements qui contient le `pom.xml` parent, lancez `git checkout exercises/part-1` si la branche active est différente. Puis lancez le build.

```sh
mvn clean install
```

**▶ Action** · Vérifiez que Maven affiche `BUILD SUCCESS` et que `dojo/target/dojo-tools.jar` existe.

**▶ Action** · Depuis le même dossier, lancez les deux commandes.

```sh
java -jar dojo/target/dojo-tools.jar prepare-copy ex1 avant ../dojo-ex1-avant
java -jar dojo/target/dojo-tools.jar prepare-copy ex1 apres ../dojo-ex1-apres
```

### B2. Tester la consigne courte dans `dojo-ex1-avant`

**▶ Action** · Ouvrez `dojo-ex1-avant` dans Bob. Dans une nouvelle conversation, envoyez ce bloc.

```text
[dojo ex1 avant]
Ajoute un champ description à la réponse de l’API quand une demande de virement est refusée. Vérifie le résultat.
```

**▶ Action** · Après la réponse finale de Bob, envoyez ce message dans la même conversation.

```text
[dojo fin] Évalue cette tentative avec le skill dojo-evaluation.
```

**▶ Action** · Ouvrez le rapport JSON au chemin affiché par Bob. Gardez-le avant de passer à B3.

**↪ Action conditionnelle** · Si Bob affiche un numéro de tentative sans générer le rapport, exécutez la commande suivante depuis le dossier ouvert dans Bob. Remplacez `NUMERO` par le numéro affiché.

```sh
java -jar dojo/target/dojo-tools.jar evaluate NUMERO
```

### B3. Tester la consigne détaillée dans `dojo-ex1-apres`

**▶ Action** · Ouvrez `dojo-ex1-apres` dans Bob. Dans une nouvelle conversation, envoyez ce bloc.

```text
[dojo ex1 apres]
Ajoute description aux refus de POST /api/v1/transfers. Conserve code et message. Compose description avec le libellé de l’erreur, un point, une espace et le message.

Vérifie 0 € et −0,01 €. Attends HTTP 400, le code err.fun.02, le message « The amount must be strictly positive. » et la description « Invalid transfer. The amount must be strictly positive. ». Après chaque refus, vérifie que les deux soldes et l’historique restent identiques.

Vérifie 0,01 € avec un solde suffisant. Attends HTTP 201, le débit, le crédit et un virement ajouté à l’historique.
```

**▶ Action** · Après la réponse finale de Bob, envoyez ce message dans la même conversation.

```text
[dojo fin] Évalue cette tentative avec le skill dojo-evaluation.
```

**▶ Action** · Ouvrez le rapport JSON au chemin affiché par Bob.

**↪ Action conditionnelle** · Si Bob affiche un numéro de tentative sans générer le rapport, exécutez la commande suivante depuis le dossier ouvert dans Bob. Remplacez `NUMERO` par le numéro affiché.

```sh
java -jar dojo/target/dojo-tools.jar evaluate NUMERO
```

### B4. Comparer vos deux rapports

**▶ Action** · Comparez les cas vérifiés, leurs résultats et les éventuelles relances. Relevez aussi le score des contrôles de référence, la durée et les appels par outil.
