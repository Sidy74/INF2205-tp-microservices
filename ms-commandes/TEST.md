# Tests — MS Commandes

## 1. Objectif

Les tests de `ms-commandes` permettent de vérifier les règles métier liées à la création et à la gestion des commandes ainsi que le comportement du contrôleur REST et de la sécurité.

## 2. Organisation

Les tests sont organisés dans :

```text
src/test/java/com/fst/ms_commandes/
├── service/
│   └── CommandeServiceTest.java
└── controller/
    └── CommandeControllerIntegrationTest.java
```

## 3. Tests unitaires

Les tests unitaires de `CommandeService` utilisent **JUnit 5** et **Mockito**.

Les principaux cas testés sont :

- création d'une commande valide ;
- rejet d'une commande sans ligne ;
- rejet d'un produit inactif ;
- rejet lorsque le stock est insuffisant ;
- récupération d'une commande existante ;
- gestion d'une commande inexistante ;
- récupération de la liste des commandes ;
- changement de statut d'une commande ;
- gestion d'une commande inexistante lors du changement de statut ;
- interdiction d'annuler une commande déjà livrée.

## 4. Tests du contrôleur

`CommandeControllerIntegrationTest` utilise `MockMvc` pour tester les endpoints REST.

Les tests vérifient notamment :

- création d'une commande par un utilisateur `CLIENT` ;
- refus d'accès sans authentification ;
- refus d'un rôle insuffisant ;
- consultation d'une commande par un `GESTIONNAIRE` ;
- refus de consultation par un `CLIENT` ;
- validation d'une quantité négative ;
- changement de statut par un utilisateur autorisé ;
- refus du changement de statut par un utilisateur non autorisé.

Les codes HTTP vérifiés comprennent :

- `201 Created`
- `200 OK`
- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`

## 5. Résultats

Commande utilisée :

```bash
mvn clean test
```

Résultat :

```text
Tests run: 20
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Les 20 tests de `ms-commandes` sont donc validés.