# Exercice 3 · Mettre en place un hook et un skill pour diagnostiquer un test

**ℹ️ Objectif** · La consultation d’un virement par UUID utilise le port `TransferRepository`. Vous allez rétablir le test ArchUnit qui protège cette règle. L’exercice se déroule sur la branche `exercises/part-3`.

**ℹ️ À savoir · Hook** · Un hook exécute automatiquement une action lorsqu’un événement se produit. Ici, l’action prévue est de transmettre à Bob le résultat du test ciblé après la commande Maven.

**ℹ️ À savoir · Skill** · Un skill décrit une démarche que Bob peut réutiliser pour une tâche. Ici, il lui indique comment rétablir le test ArchUnit, le lancer avec Maven et analyser le résultat.

> **Point de départ de l’exercice** · L’assertion ArchUnit est retirée volontairement. Le comportement de l’API reste intact. La suite de tests ne vérifie plus que la consultation appelle `TransferRepository.find`. Votre tâche consiste à rétablir ce contrôle.

**ℹ️ Information** · L’évaluation finale vérifie que votre test échoue si l’appel au port est absent.

## 1. Préparer le projet

**▶ Action** · Dans Bob, ouvrez le dossier du projet Virements qui contient le `pom.xml` parent. Dans le terminal, vérifiez que `java -version` et `mvn -v` affichent Java 21.

Si vous êtes sur une autre branche, lancez `git checkout exercises/part-3`. Puis lancez

```sh
mvn clean install
```

Vérifiez que Maven affiche `BUILD SUCCESS` et que `dojo/target/dojo-tools.jar` existe.

## 2. Configurer le hook de diagnostic

**ℹ️ Information** · Le hook doit transmettre à Bob le résultat de l’exécution de `TransferLookupArchitectureTest`, avec le nom du test et le message d’erreur si Maven échoue.

**▶ Action** · Ouvrez une nouvelle conversation dans Bob et envoyez le message ci-dessous pour configurer le hook.

```text
Repère la configuration des hooks de Bob et conserve le hook de collecte existant. Configure un hook pour transmettre à Bob le résultat de Maven après une exécution ciblée de TransferLookupArchitectureTest. En cas d’échec, indique le nom du test et le message d’erreur disponible. Indique les fichiers créés ou modifiés. Si le résultat ne peut pas être transmis par un hook, explique le blocage. Préserve le code applicatif et les tests.
```

**↪ Action conditionnelle** · Si Bob signale que le hook ne peut pas transmettre le résultat, poursuivez avec la sortie Maven affichée dans la conversation.

## 3. Créer le skill ArchUnit

**▶ Action** · Dans la même conversation, envoyez

```text
Crée un skill Bob nommé dojo-diagnostic-archunit et indique où tu l’as créé.

Guide le rétablissement de l’assertion de TransferLookupArchitectureTest qui vérifie que TransferService.getTransfer appelle TransferRepository.find. Demande de lire les tests d’architecture existants, d’écrire l’assertion et de lancer depuis la racine du projet :

mvn -pl exposition -am -Dtest=TransferLookupArchitectureTest -Dsurefire.failIfNoSpecifiedTests=false test

Si Maven échoue, lis le résultat transmis par le hook ou la sortie de la commande, identifie la cause et relance le test après correction. Termine en indiquant quelle assertion vérifie l’appel au port et quel résultat Maven a affiché. Préserve le code applicatif et les contrôles de référence du dojo.

Crée uniquement le skill.
```

## 4. Rétablir et exécuter le test avec le skill

**▶ Action** · Ouvrez une nouvelle conversation Bob et envoyez ce bloc.

```text
[dojo ex3]
Utilise le skill dojo-diagnostic-archunit. Complète le test applicationLooksUpTheTransferThroughTheOutputPort dans TransferLookupArchitectureTest pour vérifier que TransferService.getTransfer appelle TransferRepository.find. Lance le test ArchUnit ciblé avec Maven et présente le résultat. Préserve le code applicatif et les autres tests.
```

**ℹ️ Information** · Un résultat Maven vert indique que le test passe avec le code actuel. À la clôture, l’évaluation vérifie aussi que le test échoue si l’appel à `TransferRepository.find` est absent.

## 5. Clôturer et consulter le rapport

**▶ Action** · Après le résultat Maven, envoyez dans la même conversation

```text
[dojo fin] Évalue cette tentative avec le skill dojo-evaluation.
```

**▶ Action** · Ouvrez le rapport indiqué par Bob.
