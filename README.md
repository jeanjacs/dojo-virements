# Dojo Virements

Atelier IBM Bob de deux heures sur une simulation bancaire locale
avec Spring Boot 4, Java 21, Maven et H2. Les comptes fictifs **COMPTE-A** et
**COMPTE-B** démarrent respectivement à **5 000,00 €** et **150,00 €**.
La base H2 en mémoire est initialisée au démarrage.

| API | Fonction |
| --- | --- |
| `POST /api/v1/transfers` | Effectuer un virement interne à partir d’un objet JSON |
| `GET /api/v1/transfers` | Consulter l’historique local |
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
| `exposition` | API REST, bootstrap Spring Boot, initialisation H2 et Swagger |

Un module `infra-http` ne sera ajouté que si l'application doit configurer des
clients HTTP pour appeler des services externes.

`Account` et `Transfer` sont des racines d’agrégat identifiées respectivement
par le numéro de compte et l’UUID du virement. Le cas d’utilisation les orchestre,
tandis que la `TransferUnitOfWork` garantit l’enregistrement atomique des deux
comptes et du virement.

Les violations des règles métier produisent des `BusinessException` typées et
sont exposées en HTTP 400. Les adaptateurs convertissent leurs erreurs de
persistance en `TechnicalException`, exposées en HTTP 500 sans divulguer leur cause.

Le domaine définit les codes et les libellés dans `AccountErrors` et
`TransferErrors`, au sein de leurs agrégats respectifs. Au départ de l’exercice 1,
l’API renvoie le code et le message du refus.

```json
{
  "code": "err.fun.02",
  "message": "The amount must be strictly positive."
}
```

Les codes fonctionnels sont stables : `err.fun.01` pour un compte invalide,
`err.fun.02` pour un virement invalide, `err.fun.03` pour un solde insuffisant
et `err.fun.04` pour un compte introuvable. Une erreur technique est exposée
sous le code générique `err.tech.01`.

Pour construire et tester l'ensemble :

```sh
./mvnw test
```

Pour produire puis lancer l'application :

```sh
./mvnw package
java -jar exposition/target/exposition-1.0.jar
```

## Pour les participants

Les supports de présentation et le guide participant sont disponibles dans
le dossier docs/supports/ du dépôt. Votre relais vous les distribue avec
les copies préparées.

Ouvrez dans Bob la copie que votre relais vous indique. Les exercices et
l'évaluation se pilotent par messages dans le chat.
Les copies sont des bases de travail. Les modèles et les tests de référence
accompagnent les squelettes. Vous créez les ressources propres à l'exercice
avec Bob à partir du pas-à-pas remis séparément.
Si vous travaillez directement à la racine du dépôt, préparez le hook avant
d'ouvrir Bob avec `./mvnw -pl dojo -am package`. Les copies préparées contiennent
déjà le JAR.

1. Commencez par envoyer le message ci-dessous dans la copie de vérification.

   ```text
   [dojo verification]
   Ouvre pom.xml avec ton outil de lecture et indique la version de Spring Boot.
   ```

2. Envoyez `[dojo fin] Évalue cette tentative avec le skill dojo-evaluation.`
   Le rapport doit montrer une lecture observée et un contrôle réussi.
3. Suivez les fiches [Cadrer](docs/exercices/ex1.md), [Rules et ADR](docs/exercices/ex2.md)
   et [Skill et hook](docs/exercices/ex3.md). Ouvrez la copie correspondant à chaque passage.
   Les rapports restent disponibles dans chaque copie sous `.dojo-runtime/`.
   Vous pouvez déposer leurs deux JSON dans un même chat et demander à Bob de les comparer.

## Pour le relais

Prévoyez Bob IDE avec les hooks du dépôt autorisés et un JDK 21 accessible
via `java`. Vérifiez aussi que Maven Central ou le miroir
interne est accessible au Maven Wrapper. Les actions de Bob suivent les
paramètres de permissions de l'équipe. Validez ces prérequis et préchauffez le
cache Maven avant la séance.

Les branches `exercises/part-1`, `exercises/part-2` et `exercises/part-3`
conservent le code de départ. Pour un exercice, générez les deux copies depuis
la branche correspondante. Par exemple, depuis un espace de travail propre :

```sh
git switch exercises/part-1
./mvnw package
java -jar dojo/target/dojo-tools.jar prepare-copies ../binome-01-part-1
```

Ouvrez ensuite `ex1-avant` et `ex1-apres` dans le dossier généré. Le même
procédé s'applique à chaque numéro d'exercice.

Sous Windows, utilisez `mvnw.cmd` à la place de `./mvnw`.

Le hook Java écoute `UserPromptSubmit` pour les marqueurs et `PreToolUse`
pour les appels. Il conserve les identifiants, les noms d'outils et les
horodatages dans `.dojo-runtime/measure.sqlite3`. Après la clôture, le skill
utilise `java -jar dojo/target/dojo-tools.jar evaluate NUMERO` pour générer le rapport.

Le pilote dans Bob IDE doit confirmer la lecture réelle en phase de vérification,
la clôture avant le déclenchement du skill et l'absence d'appels d'évaluation
dans les compteurs. Le contrôle de vérification est distinct des validations
métier à implémenter pendant les exercices.

Voir le [déroulé d'animation](docs/animation.md).
