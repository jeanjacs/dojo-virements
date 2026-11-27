# Exercice 2 · Créer une règle de nommage et guider Bob avec un ADR

**ℹ️ Information** · Bob ajoute `GET /api/v1/transfers/{id}`. Les deux tests utilisent la même règle de nommage. Le test **Prompt détaillé** décrit l’architecture à suivre. Le test **Avec ADR** consigne ces mêmes décisions et les critères d’acceptation dans l’ADR. Le score de référence couvre les cas HTTP 200 et 404. Pour le parcours avec ADR, vérifiez aussi dans les résultats de Bob les cas HTTP 400 et 500.

**ℹ️ À savoir · Règle Bob** · Une règle donne à Bob une consigne à appliquer aux tâches du projet auxquelles elle s’applique. Ici, elle demande de nommer en anglais les classes, les méthodes et les variables Java ajoutées.

**ℹ️ À savoir · ADR** · Un ADR (*Architecture Decision Record*) consigne une décision d’architecture, les raisons du choix et ses conséquences. Ici, il explique pourquoi la consultation passe par un cas d’usage et des ports, et consigne les réponses HTTP attendues. Bob peut consulter ce fichier dans une nouvelle conversation.

## Avant de commencer · Modes de réalisation

| Votre situation | Parcours |
| --- | --- |
| L’animateur vous a affecté au groupe 1 | [Mode A · Groupe 1, prompt détaillé](#groupe-1--prompt-détaillé) |
| L’animateur vous a affecté au groupe 2 | [Mode A · Groupe 2, avec ADR](#groupe-2--avec-adr) |
| Vous réalisez l’exercice seul | [Mode B · Deux tests en autonomie](#mode-b--deux-tests-en-autonomie) |

**ℹ️ Information · Prérequis** · Git, JDK 21, Maven et IBM Bob IDE. Les hooks du projet doivent être autorisés dans Bob. Dans le terminal de Bob, `java -version` et `mvn -v` doivent afficher Java 21.

## Mode A · Un test en groupe

### Groupe 1 · Prompt détaillé

#### 1. Préparer le projet

**▶ Action** · Dans le dossier du projet Virements qui contient le `pom.xml` parent, lancez `git checkout exercises/part-2` si la branche active est différente. Ensuite, lancez

```sh
mvn clean install
```

**▶ Action** · Vérifiez que Maven affiche `BUILD SUCCESS` et que `dojo/target/dojo-tools.jar` existe.

#### 2. Créer la règle de nommage

**▶ Action** · Dans Bob, ouvrez le dossier du projet Virements qui contient le `pom.xml` parent. Dans une nouvelle conversation, envoyez

```text
Crée une règle Bob qui impose de nommer en anglais chaque classe, méthode et variable Java ajoutée. Indique le chemin du fichier créé.
```

**▶ Action** · Ouvrez le fichier indiqué par Bob et vérifiez que la règle impose des noms Java en anglais.

#### 3. Lancer et évaluer votre test

**▶ Action** · Ouvrez une nouvelle conversation Bob et envoyez ce bloc en un seul message.

```text
[dojo ex2 avant]
Ajoute GET /api/v1/transfers/{id} pour consulter un virement par UUID. Organise la recherche autour d’un cas d’usage et de ses ports.

- Exposition : valide l’UUID, appelle le port d’entrée et traduit le résultat en HTTP.
- Application : pilote la recherche via un port de sortie et traite l’absence.
- Domaine : porte le modèle et ses invariants.
- Infrastructure : implémente le port avec JPA et remonte les erreurs de persistance.

Utilise un dépôt en mémoire pour les tests applicatifs et H2 pour les tests d’intégration.
```

**▶ Action** · Après la réponse finale de Bob, envoyez ce message dans la même conversation.

```text
[dojo fin] Évalue cette tentative avec le skill dojo-evaluation.
```

**▶ Action** · Ouvrez et conservez le rapport JSON au chemin affiché par Bob.

**↪ Action conditionnelle** · Si Bob affiche un numéro de tentative sans générer de rapport, exécutez cette commande depuis le dossier ouvert dans Bob. Remplacez `NUMERO` par le numéro affiché.

```sh
java -jar dojo/target/dojo-tools.jar evaluate NUMERO
```

#### 4. Conserver votre rapport

**▶ Action** · Conservez votre rapport pour les échanges en fin de séance.

### Groupe 2 · Avec ADR

#### 1. Préparer le projet

**▶ Action** · Dans le dossier du projet Virements qui contient le `pom.xml` parent, lancez `git checkout exercises/part-2` si la branche active est différente. Ensuite, lancez

```sh
mvn clean install
```

**▶ Action** · Vérifiez que Maven affiche `BUILD SUCCESS` et que `dojo/target/dojo-tools.jar` existe.

#### 2. Créer la règle de nommage

**▶ Action** · Dans Bob, ouvrez le dossier du projet Virements qui contient le `pom.xml` parent. Dans une nouvelle conversation, envoyez

```text
Crée une règle Bob qui impose de nommer en anglais chaque classe, méthode et variable Java ajoutée. Indique le chemin du fichier créé.
```

**▶ Action** · Ouvrez le fichier indiqué par Bob et vérifiez que la règle impose des noms Java en anglais.

#### 3. Créer l’ADR

**▶ Action** · Dans la même conversation, envoyez ce message.

```text
Crée un ADR de modularisation avec les sections Contexte, Options, Décision, Conséquences, Critères d’acceptation et Tests. Indique le chemin du fichier créé.

Compare une recherche pilotée par le contrôleur et une recherche pilotée par un cas d’usage. Justifie le choix du cas d’usage.
- Exposition : valide l’UUID, appelle le port d’entrée et traduit le résultat en HTTP.
- Application : pilote la recherche via un port de sortie et traite l’absence.
- Domaine : porte le modèle et ses invariants.
- Infrastructure : implémente le port avec JPA et remonte les erreurs de persistance.

Dans les critères d’acceptation, indique les résultats attendus pour GET /api/v1/transfers/{id} :
- UUID présent dans l’historique : HTTP 200, avec les mêmes champs et valeurs que l’entrée correspondante.
- UUID mal formé : HTTP 400.
- UUID valide absent : HTTP 404.
- Erreur de persistance : HTTP 500 avec un message générique.
Mentionne le nommage Java en anglais, les tests applicatifs avec un dépôt en mémoire et les tests d’intégration avec H2.
```

**▶ Action** · Ouvrez l’ADR au chemin indiqué par Bob. Vérifiez les responsabilités des modules et les quatre critères d’acceptation.

#### 4. Lancer et évaluer votre test

**▶ Action** · Ouvrez une nouvelle conversation Bob et envoyez ce bloc en un seul message.

```text
[dojo ex2 apres]
Ajoute GET /api/v1/transfers/{id} en suivant les décisions et les critères d’acceptation de l’ADR de modularisation du dépôt.
```

**▶ Action** · Après la réponse finale de Bob, envoyez ce message dans la même conversation.

```text
[dojo fin] Évalue cette tentative avec le skill dojo-evaluation.
```

**▶ Action** · Ouvrez et conservez le rapport JSON au chemin affiché par Bob.

**↪ Action conditionnelle** · Si Bob affiche un numéro de tentative sans générer de rapport, exécutez cette commande depuis le dossier ouvert dans Bob. Remplacez `NUMERO` par le numéro affiché.

```sh
java -jar dojo/target/dojo-tools.jar evaluate NUMERO
```

#### 5. Conserver votre rapport

**▶ Action** · Conservez votre rapport pour les échanges en fin de séance.

## Mode B · Deux tests en autonomie

### B1. Générer les deux dossiers

**▶ Action** · Dans le dossier du projet Virements qui contient le `pom.xml` parent, lancez `git checkout exercises/part-2` si la branche active est différente. Ensuite, lancez

```sh
mvn clean install
```

**▶ Action** · Vérifiez que Maven affiche `BUILD SUCCESS` et que `dojo/target/dojo-tools.jar` existe.

**▶ Action** · Depuis le même dossier, lancez les deux commandes.

```sh
java -jar dojo/target/dojo-tools.jar prepare-copy ex2 avant ../dojo-ex2-avant
java -jar dojo/target/dojo-tools.jar prepare-copy ex2 apres ../dojo-ex2-apres
```

### B2. Tester le prompt détaillé dans `dojo-ex2-avant`

**▶ Action** · Ouvrez `dojo-ex2-avant` dans Bob. Dans une nouvelle conversation, envoyez

```text
Crée une règle Bob qui impose de nommer en anglais chaque classe, méthode et variable Java ajoutée. Indique le chemin du fichier créé.
```

**▶ Action** · Ouvrez le fichier indiqué par Bob et vérifiez que la règle impose des noms Java en anglais.

**▶ Action** · Ouvrez une nouvelle conversation Bob et envoyez

```text
[dojo ex2 avant]
Ajoute GET /api/v1/transfers/{id} pour consulter un virement par UUID. Organise la recherche autour d’un cas d’usage et de ses ports.

- Exposition : valide l’UUID, appelle le port d’entrée et traduit le résultat en HTTP.
- Application : pilote la recherche via un port de sortie et traite l’absence.
- Domaine : porte le modèle et ses invariants.
- Infrastructure : implémente le port avec JPA et remonte les erreurs de persistance.

Utilise un dépôt en mémoire pour les tests applicatifs et H2 pour les tests d’intégration.
```

**▶ Action** · Après la réponse finale de Bob, envoyez ce message dans la même conversation.

```text
[dojo fin] Évalue cette tentative avec le skill dojo-evaluation.
```

**▶ Action** · Ouvrez et conservez le rapport JSON au chemin affiché par Bob.

**↪ Action conditionnelle** · Si Bob affiche un numéro de tentative sans générer de rapport, exécutez cette commande depuis le dossier ouvert dans Bob. Remplacez `NUMERO` par le numéro affiché.

```sh
java -jar dojo/target/dojo-tools.jar evaluate NUMERO
```

### B3. Tester avec ADR dans `dojo-ex2-apres`

**▶ Action** · Ouvrez `dojo-ex2-apres` dans Bob. Dans une nouvelle conversation, envoyez

```text
Crée une règle Bob qui impose de nommer en anglais chaque classe, méthode et variable Java ajoutée. Indique le chemin du fichier créé.
```

**▶ Action** · Ouvrez le fichier indiqué par Bob et vérifiez que la règle impose des noms Java en anglais.

**▶ Action** · Dans le même chat, envoyez

```text
Crée un ADR de modularisation avec les sections Contexte, Options, Décision, Conséquences, Critères d’acceptation et Tests. Indique le chemin du fichier créé.

Compare une recherche pilotée par le contrôleur et une recherche pilotée par un cas d’usage. Justifie le choix du cas d’usage.
- Exposition : valide l’UUID, appelle le port d’entrée et traduit le résultat en HTTP.
- Application : pilote la recherche via un port de sortie et traite l’absence.
- Domaine : porte le modèle et ses invariants.
- Infrastructure : implémente le port avec JPA et remonte les erreurs de persistance.

Dans les critères d’acceptation, indique les résultats attendus pour GET /api/v1/transfers/{id} :
- UUID présent dans l’historique : HTTP 200, avec les mêmes champs et valeurs que l’entrée correspondante.
- UUID mal formé : HTTP 400.
- UUID valide absent : HTTP 404.
- Erreur de persistance : HTTP 500 avec un message générique.
Mentionne le nommage Java en anglais, les tests applicatifs avec un dépôt en mémoire et les tests d’intégration avec H2.
```

**▶ Action** · Ouvrez l’ADR au chemin indiqué par Bob. Vérifiez les responsabilités des modules et les quatre critères d’acceptation.

**▶ Action** · Ouvrez une nouvelle conversation Bob et envoyez

```text
[dojo ex2 apres]
Ajoute GET /api/v1/transfers/{id} en suivant les décisions et les critères d’acceptation de l’ADR de modularisation du dépôt.
```

**▶ Action** · Après la réponse finale de Bob, envoyez ce message dans la même conversation.

```text
[dojo fin] Évalue cette tentative avec le skill dojo-evaluation.
```

**▶ Action** · Ouvrez et conservez le rapport JSON au chemin affiché par Bob.

**↪ Action conditionnelle** · Si Bob affiche un numéro de tentative sans générer de rapport, exécutez cette commande depuis le dossier ouvert dans Bob. Remplacez `NUMERO` par le numéro affiché.

```sh
java -jar dojo/target/dojo-tools.jar evaluate NUMERO
```

### B4. Comparer vos deux rapports

**▶ Action** · Comparez les cas HTTP que Bob a identifiés et ceux qu’il a effectivement testés. Pour le test avec ADR, vérifiez dans le chat si Bob a lu le fichier et dans le code quelles décisions il a appliquées. Comparez aussi la durée et les appels par outil.
