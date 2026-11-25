# Dojo Virements

Simulation bancaire locale Spring Boot 4, Java 21 et Maven, en euros avec des comptes fictifs.

- `domaine/` porte les agrégats `Account` et `Transfer` et leurs invariants. `application/` expose le cas d'utilisation
  et les ports. `infra-jpa/` persiste dans H2, et `exposition/` porte les adaptateurs entrants,
  assemble et démarre l'application Spring Boot.
- `POST /transfers` effectue un virement. `GET /transfers` liste l'historique.
  `GET /accounts/{accountNumber}` renvoie le solde.
- Utilise des `BigDecimal` pour les montants. Les soldes et l'historique doivent rester
  cohérents. Toute demande refusée préserve ces trois valeurs.
- Le Maven Wrapper lance les tests de développement. Après la clôture, le skill
  `dojo-evaluation` exécute les contrôles de référence.
- Préserve `dojo/reference/`, `dojo/checks/`, le collecteur et le vérificateur Java
  dans `dojo/src/main/` ainsi que les hooks de référence. Le fichier à corriger
  pour l’exercice 3 est `dojo/ex3/FixtureReader.java`.
- Les marqueurs de mesure figurent dans la première ligne du message. Après
  `[dojo fin]`, utilise l'identifiant indiqué par le hook avec `dojo-evaluation`.
