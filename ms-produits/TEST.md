# Tests — MS Produits

## 1. Objectif

Les tests de `ms-produits` permettent de vérifier la logique métier du service produit ainsi que le comportement du contrôleur REST, notamment la validation des requêtes et les règles de sécurité.

## 2. Organisation

Les tests sont organisés dans :

```text
src/test/java/com/fst/ms_produits/
├── service/
│   └── ProduitServiceTest.java
└── controller/
    └── ProduitControllerIntegrationTest.java
```

## 3. Tests unitaires

Les tests unitaires de `ProduitService` utilisent **JUnit 5** et **Mockito** afin d'isoler le service de la base de données.

Les principaux cas testés sont :

- création d'un produit avec une référence disponible ;
- rejet d'une référence déjà existante ;
- récupération d'un produit existant ;
- gestion d'un produit inexistant ;
- vérification de la disponibilité d'un produit ;
- gestion d'un stock insuffisant ;
- gestion d'un produit inactif ;
- suppression d'un produit existant ;
- gestion de la suppression d'un produit inexistant.

## 4. Tests du contrôleur

`ProduitControllerIntegrationTest` utilise `MockMvc` pour tester les endpoints REST.

Les tests vérifient notamment :

- accès autorisé à un produit ;
- accès sans authentification ;
- accès avec un rôle insuffisant ;
- comportement HTTP et contenu JSON retourné.

Les codes HTTP vérifiés comprennent notamment :

- `200 OK`
- `401 Unauthorized`
- `403 Forbidden`

## 5. Résultats

Commande utilisée :

```bash
mvn clean test
```

Résultat :

```text
Tests run: 13
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Les 13 tests de `ms-produits` sont donc validés.