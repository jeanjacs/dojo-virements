# Dojo Virements

Atelier IBM Bob de deux heures sur une simulation bancaire locale
avec Spring Boot 4, Java 21, Maven et H2. Les comptes fictifs **COMPTE-A** et
**COMPTE-B** démarrent respectivement à **5 000,00 €** et **150,00 €**.
La base H2 en mémoire est initialisée au démarrage.

| API | Fonction |
| --- | --- |
| `POST /api/v1/transfers` | Effectuer un virement interne à partir d’un objet JSON |
| `GET /api/v1/transfers` | Consulter l’historique local |
| `GET /api/v1/transfers/{id}` | Consulter un virement par UUID |
| `GET /api/v1/accounts/{accountNumber}` | Consulter le solde d’un compte |

L'accueil `/` redirige vers Swagger UI. Le contrat OpenAPI est également
disponible sur `/v3/api-docs`.

La console H2 est disponible sur `/h2-console/`. Utilisez exactement l’URL JDBC
`jdbc:h2:mem:virements`, l’utilisateur `sa` et un mot de passe vide. Une autre URL
JDBC ouvre une base mémoire distincte et donc vide.

Exemple d’objet JSON à envoyer à l’API :

```json
{"source":"COMPTE-A","destination":"COMPTE-B","amount":25.37}
```

Le socle débite et crédite les comptes au centime près et refuse les virements
en cas de solde insuffisant. Le montant doit être strictement positif et ne peut
pas dépasser **1 000,00 €**.

## Architecture

Le projet est organisé en modules Maven selon une architecture hexagonale :

| Module | Responsabilité |
| --- | --- |
| `domaine` | Racines d’agrégat `Account` et `Transfer`, et invariants métier |
| `application` | Cas d'utilisation, repositories et unit of work |
| `infra-jpa` | Adaptateurs de persistance JPA/H2 |
| `exposition` | API REST, démarrage Spring Boot, initialisation H2 et Swagger |
| `dojo` | Collecte des tentatives et contrôles de référence |

`Account` et `Transfer` sont des racines d’agrégat identifiées respectivement
par le numéro de compte et l’UUID du virement. Le cas d’utilisation les orchestre,
tandis que la `TransferUnitOfWork` garantit l’enregistrement atomique des deux
comptes et du virement.

Les violations des règles métier produisent des `BusinessException` typées et
sont exposées en HTTP 400. Les adaptateurs convertissent leurs erreurs de
persistance en `TechnicalException`, exposées en HTTP 500 sans divulguer leur cause.

Le domaine définit les codes et les libellés dans `AccountErrors` et
`TransferErrors`, au sein de leurs agrégats respectifs. L’API renvoie le code, le message et une description du refus.

```json
{
  "code": "err.fun.02",
  "message": "The amount must be strictly positive.",
  "description": "Invalid transfer. The amount must be strictly positive."
}
```

Les codes fonctionnels sont stables : `err.fun.01` pour un compte invalide,
`err.fun.02` pour un virement invalide, `err.fun.03` pour un solde insuffisant
`err.fun.04` pour un compte introuvable et `err.fun.05` pour un virement introuvable. Le virement introuvable est renvoyé en HTTP 404. Une erreur technique est exposée
sous le code générique `err.tech.01`.

## Tester et lancer l’application

Depuis le dossier qui contient le `pom.xml` parent, lancez les tests avec Maven.

```sh
mvn test
```

Pour compiler le projet, générer les JAR et lancer l’application, exécutez

```sh
mvn clean install
java -jar exposition/target/exposition-1.0.jar
```

## Ouvrir le projet dans Bob

Ouvrez dans Bob le dossier du projet Virements qui contient le `pom.xml` parent.
Le contexte du projet figure dans `AGENTS.md`. Les règles, les hooks de collecte
et le skill d’évaluation sont fournis dans `.bob/`. Vous pouvez suivre les fiches
avec ces fichiers, sans lancer `/init`.

Dans le terminal de Bob, vérifiez que `java -version` et `mvn -v` affichent
Java 21. La commande `mvn clean install` utilise le Maven installé sur votre
poste et génère `dojo/target/dojo-tools.jar` pour la collecte.

### Activer les hooks de collecte

1. Ouvrez **Bob Settings**, puis l’onglet **Hooks**.
2. Dans les hooks du projet, repérez `UserPromptSubmit` et `PreToolUse`.
   Les deux exécutent `java -jar dojo/target/dojo-tools.jar hook`.
3. Activez chaque entrée si elle est désactivée. Si les entrées sont absentes,
   vérifiez que le dossier ouvert dans Bob contient le `pom.xml` parent et
   `.bob/settings.json`.

Si le hook utilise une autre version de Java, envoyez « Prépare Java pour le dojo »
à Bob avant de lancer une tentative.

[Documentation IBM Bob sur les hooks](https://bob.ibm.com/fr/docs/ide/configuration/lifecycle-hooks)

## Exercices

Ouvrez la fiche de votre exercice pour suivre les commandes et les prompts.

| Fiche | Branche |
| --- | --- |
| [Exercice 1 · Cadrer un prompt pour guider Bob](docs/exercices/ex1.md) | `exercises/part-1` |
| [Exercice 2 · Créer une règle de nommage et guider Bob avec un ADR](docs/exercices/ex2.md) | `exercises/part-2` |
| [Exercice 3 · Mettre en place un hook et un skill pour diagnostiquer un test](docs/exercices/ex3.md) | `exercises/part-3` |

Dans Bob, envoyez le marqueur de début fourni dans votre fiche. Envoyez
`[dojo fin]` dans la même conversation, avec la demande d’évaluation indiquée
dans la fiche. Bob vous indique ensuite le chemin du rapport.

Le guide d’animation et le diaporama sont disponibles séparément du projet.
