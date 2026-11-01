# Tests — MS Commandes

## 1. Objectif

Les tests de `ms-commandes` permettent de vérifier les règles métier liées à la création et à la gestion des commandes ainsi que le comportement du contrôleur REST et de la sécurité.

Les tests couvrent également l'ajout de lignes à une commande existante et les autorisations associées.

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
- consultation des commandes d'un client ;
- gestion d'une commande inexistante ;
- récupération de la liste des commandes ;
- changement de statut d'une commande ;
- gestion d'une commande inexistante lors du changement de statut ;
- interdiction d'annuler une commande déjà livrée ;
- ajout d'une ligne à une commande existante ;
- ajout d'un nouveau produit à une commande ;
- augmentation de la quantité lorsque le produit existe déjà avec le même prix ;
- création d'une nouvelle ligne lorsque le prix actuel du produit est différent ;
- rejet de l'ajout d'une ligne lorsque la commande n'est pas `EN_ATTENTE` ;
- rejet d'un produit inactif lors de l'ajout d'une ligne ;
- rejet lorsque le stock disponible est insuffisant pour la quantité ajoutée.

## 4. Tests du contrôleur

`CommandeControllerIntegrationTest` utilise `MockMvc` pour tester les endpoints REST et les règles de sécurité.

Les tests vérifient notamment :

- création d'une commande par un utilisateur `CLIENT` ;
- refus d'accès sans authentification ;
- refus d'un rôle insuffisant ;
- consultation d'une commande par un `GESTIONNAIRE` ;
- consultation des commandes d'un client par un `GESTIONNAIRE` ;
- refus de consultation par un `CLIENT` ;
- validation d'une quantité négative ;
- changement de statut par un utilisateur autorisé ;
- refus du changement de statut par un utilisateur non autorisé ;
- ajout d'une ligne par un `GESTIONNAIRE` ;
- ajout d'une ligne par un `ADMIN` ;
- refus de l'ajout d'une ligne par un `CLIENT`.

Les codes HTTP vérifiés comprennent :

- `201 Created`
- `200 OK`
- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`

## 5. Sécurité testée

Les autorisations liées à l'ajout d'une ligne sont également vérifiées :

| Opération | CLIENT | GESTIONNAIRE | ADMIN |
|---|:---:|:---:|:---:|
| Créer une commande | ✅ | ❌ | ❌ |
| Consulter les commandes | ❌ | ✅ | ✅ |
| Ajouter une ligne | ❌ | ✅ | ✅ |
| Modifier le statut | ❌ | ✅ | ✅ |

Un utilisateur `CLIENT` qui tente d'ajouter une ligne reçoit :

```text
403 Forbidden
```

Un utilisateur `GESTIONNAIRE` ou `ADMIN` peut effectuer cette opération.

## 6. Résultats

Commande utilisée :

```bash
mvn clean test
```

Résultat :

```text
Tests run: 26
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

Les **26 tests de `ms-commandes`** sont donc validés.

## 7. Couverture fonctionnelle

Les tests permettent de vérifier les principales règles métier du microservice :

```text
Création commande
       │
       ├── au moins une ligne
       ├── produit existant
       ├── produit actif
       ├── stock disponible
       └── calcul du montant total

Ajout ligne
       │
       ├── commande existante
       ├── statut EN_ATTENTE
       ├── produit actif
       ├── stock disponible
       ├── même produit + même prix → quantité augmentée
       ├── même produit + prix différent → nouvelle ligne
       └── nouveau produit → nouvelle ligne

Sécurité
       │
       ├── CLIENT
       ├── GESTIONNAIRE
       ├── ADMIN
       ├── 401 Unauthorized
       └── 403 Forbidden
```

## 8. Conclusion

Les tests unitaires et les tests d'intégration du contrôleur confirment le bon fonctionnement de `ms-commandes`.

Les règles métier principales, la validation des données, la gestion des erreurs et les autorisations liées aux rôles ont été vérifiées avec succès.

Le projet obtient actuellement :

```text
26 tests
0 échec
0 erreur
BUILD SUCCESS
```