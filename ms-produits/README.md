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
- vérification de la disponibilité d'un produit ;
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
- Springdoc OpenAPI
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

Le Config Server récupère les fichiers de configuration depuis le dépôt Git séparé :

```text
INF2205-tp-microservices-config-repo
```

Le fichier correspondant à ce microservice est :

```text
ms-produits.yml
```

Il contient notamment :

- le port du microservice ;
- les paramètres de connexion PostgreSQL ;
- les paramètres JPA ;
- les paramètres nécessaires à Eureka.

Le dépôt de configuration étant privé, l'accès du Config Server est effectué à l'aide de la variable d'environnement `GITHUB_TOKEN`.

Les informations sensibles ne sont pas stockées directement dans le dépôt principal.

---

## Sécurité

La sécurité est basée sur **Keycloak**, OAuth2 et JWT.

### Keycloak

Realm utilisé :

```text
gestion-commerciale
```

Client :

```text
gestion-commerciale-app
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

---

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

---

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

---

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

---

## Validation des données

Les données reçues lors de la création ou de la modification d'un produit sont validées à l'aide de **Jakarta Validation**.

Les principales règles sont :

- la référence ne doit pas être vide ;
- le nom ne doit pas être vide ;
- le prix doit être supérieur ou égal à zéro ;
- la quantité en stock doit être supérieure ou égale à zéro ;
- le champ `actif` doit être renseigné.

Une donnée invalide entraîne une réponse :

```text
400 Bad Request
```

---

## Architecture de sécurité

Le Gateway constitue le point d'entrée normal de l'application, mais `ms-produits` ne fait pas aveuglément confiance aux requêtes provenant du Gateway.

```text
Client
   |
   | JWT
   v
API Gateway
   |
   | Routage
   v
ms-produits
   |
   | Validation JWT + rôles
   v
PostgreSQL
```

Le microservice valide lui-même le JWT reçu.

Cette double protection permet également de protéger le microservice contre un accès direct contournant le Gateway.

---

## Base de données

La base PostgreSQL utilisée par `ms-produits` est :

```text
Base : ms_produits_db
Utilisateur : fst_user
Port hôte : 5433
```

La base est lancée avec Docker Compose depuis la racine du projet.

Le microservice `ms-produits` possède sa propre base de données et ne partage pas directement ses tables avec `ms-commandes`.

---

## Gestion des erreurs

Le microservice utilise un gestionnaire global des exceptions pour notamment gérer :

- produit introuvable : `404 Not Found` ;
- référence de produit déjà existante : `409 Conflict` ;
- données de produit invalides : `400 Bad Request`.

Les erreurs sont retournées sous forme de réponses HTTP adaptées afin de faciliter leur traitement par l'API Gateway et les autres services.

---

## Tests

Les tests automatisés couvrent notamment :

- création d'un produit ;
- récupération d'un produit ;
- récupération d'un produit inexistant ;
- liste des produits ;
- recherche par nom ;
- liste des produits actifs ;
- modification d'un produit ;
- suppression d'un produit ;
- vérification de disponibilité ;
- pagination et tri ;
- validation des données ;
- authentification ;
- autorisation selon les rôles.

### Résultat

```text
ms-produits

13 tests
0 échec
0 erreur

BUILD SUCCESS
```

Les tests fonctionnels complémentaires ont également été réalisés via Postman à travers l'API Gateway.

---

## Documentation OpenAPI / Swagger

Le microservice utilise **Springdoc OpenAPI** pour documenter son API.

La documentation OpenAPI peut être consultée directement :

```text
http://localhost:8082/v3/api-docs
```

En utilisation normale, elle est également exposée à travers l'API Gateway.

Swagger UI :

```text
http://localhost:8081/swagger-ui/index.html
```

---

## Tests de sécurité

Les principaux scénarios testés sont :

- requête sans token → `401 Unauthorized` ;
- CLIENT → consultation des produits autorisée ;
- CLIENT → création refusée ;
- CLIENT → modification refusée ;
- CLIENT → suppression refusée ;
- GESTIONNAIRE → création autorisée ;
- GESTIONNAIRE → modification autorisée ;
- GESTIONNAIRE → suppression refusée ;
- ADMIN → suppression autorisée.

---

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

---

## État

✅ **Fonctionnel**

Les fonctionnalités principales du microservice sont opérationnelles.

Le service :

- est enregistré auprès d'Eureka ;
- récupère sa configuration via Config Server ;
- utilise une configuration centralisée stockée dans un dépôt Git séparé ;
- utilise PostgreSQL ;
- est protégé par Spring Security ;
- valide les JWT émis par Keycloak ;
- applique les autorisations selon les rôles `CLIENT`, `GESTIONNAIRE` et `ADMIN` ;
- fournit une API documentée avec OpenAPI ;
- possède des tests automatisés validés.