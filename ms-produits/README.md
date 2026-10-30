# ms-produits

Microservice de gestion du catalogue des produits dans le cadre du TP **INF2205 — Technologies du Web**.

## Rôle

`ms-produits` est responsable de la gestion du catalogue des produits :

- création d'un produit ;
- consultation d'un produit par son identifiant ;
- liste des produits ;
- liste des produits actifs ;
- recherche par nom ;
- modification d'un produit ;
- suppression d'un produit ;
- vérification de la disponibilité d'un produit.
- pagination et tri des produits.

## Technologies

- Java 21
- Spring Boot 4.1.2
- Spring Web
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- PostgreSQL
- Spring Cloud
- Eureka Discovery Client
- Spring Cloud Config Client
- Spring Boot Actuator
- Jakarta Validation
- Lombok
- Maven

## Communication et infrastructure

`ms-produits` utilise :

- **Spring Cloud Config Client** pour récupérer sa configuration centralisée ;
- **Eureka Discovery Client** pour s'enregistrer auprès d'Eureka ;
- **PostgreSQL** pour la persistance des produits ;
- **Keycloak** comme serveur d'authentification et d'émission des JWT ;
- **Spring Security OAuth2 Resource Server** pour valider directement les JWT.

```text
                         Keycloak
                       Port : 8080
                            |
                            | JWT
                            v
                    +----------------+
                    |  API Gateway   |
                    |   Port : 8081  |
                    +----------------+
                            |
                            v
                    +----------------+
                    |  ms-produits   |
                    |   Port : 8082  |
                    +----------------+
                       |          |
                       |          |
                       v          v
                PostgreSQL     Eureka
                 Port 5433    Port 8761
```

La configuration de `ms-produits` est récupérée depuis le **Config Server** :

```text
Config Server : 8888
       |
       v
ms-produits : 8082
       |
       v
PostgreSQL : 5433

ms-produits
     |
     v
Eureka Server : 8761
```

## Sécurité

La sécurité est basée sur **Keycloak**, OAuth2 et JWT.

### Keycloak

Realm utilisé :

```text
gestion-commerciale
```

Rôles :

- `CLIENT`
- `GESTIONNAIRE`
- `ADMIN`

`ms-produits` est configuré comme **OAuth2 Resource Server**.

Il valide directement les JWT émis par Keycloak à partir de l'issuer :

```text
http://localhost:8080/realms/gestion-commerciale
```

Les rôles présents dans `realm_access.roles` du JWT sont convertis en autorités Spring Security.

### Autorisations

| Opération | CLIENT | GESTIONNAIRE | ADMIN |
|---|:---:|:---:|:---:|
| Consulter les produits | ✅ | ✅ | ✅ |
| Créer un produit | ❌ | ✅ | ✅ |
| Modifier un produit | ❌ | ✅ | ✅ |
| Supprimer un produit | ❌ | ❌ | ✅ |

Les requêtes sans JWT valide sont refusées avec :

```text
401 Unauthorized
```

Un utilisateur authentifié mais ne possédant pas le rôle requis reçoit :

```text
403 Forbidden
```

> Les mots de passe, tokens JWT et autres informations sensibles ne sont pas stockés dans le dépôt Git.

## Modèle Produit

Un produit contient notamment :

| Champ | Description |
|---|---|
| `id` | Identifiant du produit |
| `reference` | Référence unique du produit |
| `nom` | Nom du produit |
| `description` | Description du produit |
| `prix` | Prix du produit |
| `quantiteStock` | Quantité disponible en stock |
| `actif` | Indique si le produit est actif |

## Endpoints

| Méthode | Endpoint | Description | Accès |
|---|---|---|---|
| `POST` | `/api/produits` | Créer un produit | GESTIONNAIRE / ADMIN |
| `GET` | `/api/produits` | Lister les produits | CLIENT / GESTIONNAIRE / ADMIN |
| `GET` | `/api/produits/{id}` | Obtenir un produit | CLIENT / GESTIONNAIRE / ADMIN |
| `GET` | `/api/produits/actifs` | Lister les produits actifs | CLIENT / GESTIONNAIRE / ADMIN |
| `GET` | `/api/produits/recherche?nom=...` | Rechercher par nom | CLIENT / GESTIONNAIRE / ADMIN |
| `GET` | `/api/produits/{id}/disponible?quantite=...` | Vérifier la disponibilité | CLIENT / GESTIONNAIRE / ADMIN |
| `PUT` | `/api/produits/{id}` | Modifier un produit | GESTIONNAIRE / ADMIN |
| `DELETE` | `/api/produits/{id}` | Supprimer un produit | ADMIN |
| `GET` | `/api/produits/page?page=0&size=10&sortBy=id&direction=asc` | Lister les produits avec pagination et tri | CLIENT / GESTIONNAIRE / ADMIN |


## Pagination et tri

Un endpoint dédié permet de consulter le catalogue avec pagination et tri, sans modifier le comportement de l'endpoint classique `GET /api/produits`.

### Endpoint

```text
GET /api/produits/page
```

### Paramètres

| Paramètre | Valeur par défaut | Description |
|---|---:|---|
| `page` | `0` | Numéro de la page, commençant à 0 |
| `size` | `10` | Nombre de produits par page |
| `sortBy` | `id` | Champ utilisé pour le tri |
| `direction` | `asc` | Direction du tri : `asc` ou `desc` |

### Exemple

```text
GET /api/produits/page?page=0&size=5&sortBy=nom&direction=asc
```

La réponse utilise la pagination de Spring Data et contient notamment :

- `content` : produits de la page courante ;
- `totalElements` : nombre total de produits ;
- `totalPages` : nombre total de pages ;
- `number` : numéro de la page courante ;
- `size` : taille de la page ;
- `sort` : informations concernant le tri.

### Exemple de résultat testé

```text
HTTP 200 OK

page = 0
size = 5
sortBy = nom
direction = asc
totalElements = 3
totalPages = 1
```

Le tri par nom croissant a été vérifié avec succès.

L'implémentation repose sur `Page`, `PageRequest`, `Pageable` et `Sort` de Spring Data JPA.
## Architecture de sécurité

Le Gateway constitue le point d'entrée normal de l'application, mais `ms-produits` ne fait pas aveuglément confiance aux requêtes provenant du Gateway.

```text
Client
   |
   | JWT
   v
API Gateway
   |
   | Validation JWT + rôles
   |
   v
ms-produits
   |
   | Validation JWT
   |
   v
PostgreSQL
```

Cette double validation permet également de protéger le microservice contre un accès direct contournant le Gateway.

## Base de données

La base PostgreSQL utilisée par `ms-produits` est :

```text
Base : ms_produits_db
Utilisateur : fst_user
Port hôte : 5433
```

La base est lancée avec Docker Compose depuis la racine du projet.

## Gestion des erreurs

Le microservice utilise un gestionnaire global des exceptions pour notamment gérer :

- produit introuvable : `404 NOT FOUND` ;
- référence de produit déjà existante : `409 CONFLICT` ;
- données de produit invalides : `400 BAD REQUEST`.

## Tests de sécurité

Les principaux scénarios testés sont :

- requête sans token → `401 Unauthorized` ;
- CLIENT → consultation des produits autorisée ;
- CLIENT → création/modification/suppression refusée selon les règles ;
- GESTIONNAIRE → création et modification autorisées ;
- GESTIONNAIRE → suppression refusée ;
- ADMIN → suppression autorisée.

## Port

Le microservice écoute sur :

```text
8082
```

URL locale directe :

```text
http://localhost:8082
```

En utilisation normale, les requêtes passent par l'API Gateway :

```text
http://localhost:8081
```

## État

✅ **Fonctionnel**

Les fonctionnalités principales du microservice sont opérationnelles.

Le service :

- est enregistré auprès d'Eureka ;
- récupère sa configuration via Config Server ;
- utilise PostgreSQL ;
- est protégé par Spring Security ;
- valide les JWT émis par Keycloak ;
- applique les autorisations selon les rôles `CLIENT`, `GESTIONNAIRE` et `ADMIN`.