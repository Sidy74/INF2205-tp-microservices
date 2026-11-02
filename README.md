# INF2205 — TP Microservices avec Spring Boot

## 📌 Présentation

Ce projet est réalisé dans le cadre du TP **INF2205 — Technologies du Web**.

L'objectif est de mettre en place une architecture **microservices** avec **Spring Boot** et **Spring Cloud**, permettant de gérer des produits et des commandes dans une architecture distribuée.

L'application est composée de plusieurs services indépendants communiquant entre eux à travers une architecture basée sur :

- Spring Boot
- Spring Cloud
- Eureka Server
- Spring Cloud Config
- OpenFeign
- Spring Cloud Gateway
- Keycloak
- PostgreSQL
- Docker / Docker Compose

L'architecture respecte le principe suivant :

```text
                         ┌───────────────┐
                         │   Keycloak    │
                         │     :8080     │
                         └───────┬───────┘
                                 │ JWT
                                 ▼
┌──────────────┐        ┌─────────────────┐
│   Client /   │───────▶│   API Gateway   │
│   Postman    │        │      :8081      │
└──────────────┘        └────────┬────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
                    ▼                         ▼
          ┌─────────────────┐       ┌─────────────────┐
          │   ms-produits   │       │   ms-commandes  │
          │      :8082      │◀──────│      :8083      │
          └────────┬────────┘ Feign └────────┬────────┘
                   │                         │
                   ▼                         ▼
          ┌─────────────────┐       ┌─────────────────┐
          │ PostgreSQL      │       │ PostgreSQL      │
          │ produits :5433  │       │ commandes :5434 │
          └─────────────────┘       └─────────────────┘


                    ┌─────────────────────┐
                    │    Eureka Server    │
                    │        :8761        │
                    └─────────────────────┘

                    ┌─────────────────────┐
                    │    Config Server    │
                    │        :8888        │
                    └─────────────────────┘
```

---

## 🎯 Objectifs

Le projet permet de mettre en pratique les principaux concepts d'une architecture microservices :

- séparation des responsabilités ;
- découverte dynamique des services ;
- configuration centralisée ;
- communication entre microservices ;
- routage via une API Gateway ;
- authentification et autorisation ;
- gestion des rôles ;
- validation des données ;
- gestion centralisée des erreurs ;
- conteneurisation des bases de données ;
- tests des différents scénarios fonctionnels et de sécurité.

---

## 🧩 Architecture

Le projet contient les composants suivants :

| Composant | Rôle | Port |
|---|---|---:|
| Keycloak | Authentification / autorisation | `8080` |
| API Gateway | Point d'entrée unique | `8081` |
| ms-produits | Gestion des produits | `8082` |
| ms-commandes | Gestion des commandes | `8083` |
| Config Server | Configuration centralisée | `8888` |
| Eureka Server | Découverte des services | `8761` |
| PostgreSQL produits | Base de données produits | `5433` |
| PostgreSQL commandes | Base de données commandes | `5434` |

---

## 🛠️ Technologies utilisées

### Backend

- Java 21
- Spring Boot 4.1.2
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security
- Spring Boot Actuator

### Spring Cloud

- Spring Cloud Config
- Spring Cloud Netflix Eureka
- Spring Cloud OpenFeign
- Spring Cloud Gateway

### Sécurité

- Keycloak
- OAuth2
- JWT
- Spring Security Resource Server

### Base de données

- PostgreSQL 17

### Infrastructure

- Docker
- Docker Compose

### Tests et outils

- Postman
- cURL
- Maven
- Git / GitHub

---

## 📁 Structure du projet

```text
INF2205-tp-microservices/
│
├── api-gateway/
│   ├── src/
│   ├── pom.xml
│   └── ...
│
├── config-server/
│   ├── src/
│   ├── pom.xml
│   └── ...
│
├── eureka-server/
│   ├── src/
│   ├── pom.xml
│   └── ...
│
├── ms-produits/
│   ├── src/
│   ├── pom.xml
│   ├── README.md
│   └── ...
│
├── ms-commandes/
│   ├── src/
│   ├── pom.xml
│   ├── README.md
│   └── ...
│
├── postman/
│   └── INF2205-microservices.postman_collection.json
│
├── captures/
│   ├── Eureka-Screenshot.png
│   ├── Keycloak-Screenshot-01.png
│   ├── Keycloak-Screenshot-02.png
│   └── Keycloak-Screenshot-03.png
│
├── docker-compose.yml
├── .env.example
├── README.md
└── START-README.md
```

### Dépôt de configuration

La configuration centralisée du projet est stockée dans un dépôt Git séparé :

```text
INF2205-tp-microservices-config-repo/
│
├── application.yml
├── ms-produits.yml
├── ms-commandes.yml
├── api-gateway.yml
└── eureka-server.yml
```

Le **Config Server** récupère automatiquement ces fichiers depuis ce dépôt Git.

Le dépôt de configuration est indépendant du dépôt principal et n'a pas besoin d'être cloné séparément pour lancer l'application.

---

# 📦 Microservice `ms-produits`

Le microservice `ms-produits` est responsable de la gestion du catalogue des produits.

## Fonctionnalités

- créer un produit ;
- consulter un produit ;
- lister les produits ;
- rechercher un produit par nom ;
- lister les produits actifs ;
- modifier un produit ;
- supprimer un produit ;
- vérifier la disponibilité d'un produit ;
- consulter les produits avec pagination et tri.

## Endpoints principaux

```text
POST   /api/produits
GET    /api/produits
GET    /api/produits/{id}
GET    /api/produits/recherche?nom=...
GET    /api/produits/actifs
PUT    /api/produits/{id}
DELETE /api/produits/{id}
GET    /api/produits/{id}/disponible?quantite=...
GET    /api/produits/page?page=0&size=10&sortBy=id&direction=asc
```

## Modèle produit

Un produit contient notamment :

```text
id
reference
nom
description
prix
quantiteStock
actif
```

Les données sont stockées dans une base PostgreSQL dédiée.

---

# 🛒 Microservice `ms-commandes`

Le microservice `ms-commandes` est responsable de la gestion des commandes.

Une commande contient plusieurs lignes de commande.

## Fonctionnalités

- créer une commande ;
- consulter une commande ;
- lister les commandes ;
- consulter les commandes d'un client ;
- ajouter des lignes à une commande ;
- modifier le statut d'une commande ;
- vérifier l'existence du produit ;
- vérifier que le produit est actif ;
- vérifier la disponibilité du stock ;
- récupérer le prix depuis `ms-produits` ;
- calculer le montant total ;
- gérer les erreurs lorsque `ms-produits` est indisponible.

## Endpoints principaux

```text
POST  /api/commandes
GET   /api/commandes
GET   /api/commandes/{id}
GET   /api/commandes/client/{clientId}
PATCH /api/commandes/{id}/statut?statut=...
POST  /api/commandes/{id}/lignes
```

Lors de la création d'une commande, le `clientId` n'est pas fourni par le client dans le corps de la requête.

Il est récupéré depuis le claim `sub` du JWT authentifié.

Cela permet d'éviter qu'un utilisateur puisse créer une commande au nom d'un autre client.

---

# 🔗 Communication avec OpenFeign

`ms-commandes` ne possède pas directement les données de `ms-produits`.

Il communique avec `ms-produits` à travers **OpenFeign**.

```text
ms-commandes
     │
     │ OpenFeign
     ▼
ms-produits
     │
     ▼
PostgreSQL produits
```

Le client Feign permet notamment de :

- récupérer les informations d'un produit ;
- vérifier sa disponibilité.

La découverte de `ms-produits` est réalisée grâce à Eureka.

```java
@FeignClient(name = "ms-produits")
public interface ProduitClient {
    ...
}
```

Le token JWT de l'utilisateur authentifié est également transmis lors des appels Feign afin que `ms-produits` puisse appliquer ses propres règles de sécurité.

Cette approche permet d'éviter un accès direct de `ms-commandes` à la base de données de `ms-produits`.

---

# 🔎 Eureka Server

Eureka joue le rôle de **service discovery**.

Les services suivants s'enregistrent auprès d'Eureka :

```text
MS-PRODUITS
MS-COMMANDES
API-GATEWAY
```

Eureka permet aux services de se retrouver dynamiquement sans dépendre directement d'une adresse IP fixe.

Interface :

```text
http://localhost:8761
```

---

# ⚙️ Config Server

Le **Spring Cloud Config Server** centralise la configuration des différents services.

Les configurations sont stockées dans un **dépôt Git séparé** :

```text
INF2205-tp-microservices-config-repo/
```

Les fichiers de configuration sont :

```text
application.yml
ms-produits.yml
ms-commandes.yml
api-gateway.yml
eureka-server.yml
```

Le Config Server récupère automatiquement ces fichiers depuis GitHub.

Le dépôt de configuration étant privé, un `GITHUB_TOKEN` est utilisé pour permettre au Config Server d'y accéder.

Les informations sensibles ne sont pas stockées en clair dans le dépôt principal.

Le Config Server fonctionne sur :

```text
http://localhost:8888
```

Cette architecture permet de centraliser la configuration et d'éviter de dupliquer les paramètres dans chaque microservice.

---

# 🌐 API Gateway

L'API Gateway constitue le point d'entrée principal de l'application.

Les clients n'ont pas besoin d'appeler directement les microservices.

### Produits

```text
/api/produits/**
        ↓
ms-produits
```

### Commandes

```text
/api/commandes/**
        ↓
ms-commandes
```

Les routes utilisent la découverte Eureka :

```text
lb://ms-produits
lb://ms-commandes
```

L'API Gateway est accessible sur :

```text
http://localhost:8081
```

L'API Gateway participe également à la sécurisation des accès avec les JWT émis par Keycloak.

---

# 🔐 Sécurité avec Keycloak

L'authentification et l'autorisation sont assurées par **Keycloak**.

Le projet utilise le realm :

```text
gestion-commerciale
```

Client :

```text
gestion-commerciale-app
```

## Rôles

Trois rôles sont utilisés :

```text
CLIENT
GESTIONNAIRE
ADMIN
```

Les utilisateurs authentifiés obtiennent un token JWT contenant leurs rôles.

Les microservices `ms-produits` et `ms-commandes` fonctionnent comme **OAuth2 Resource Server** et valident les JWT émis par Keycloak.

Le `clientId` utilisé lors de la création d'une commande est récupéré depuis le claim `sub` du JWT.

---

## 🔑 Autorisations

### Produits

| Opération | CLIENT | GESTIONNAIRE | ADMIN |
|---|:---:|:---:|:---:|
| Consulter | ✅ | ✅ | ✅ |
| Créer | ❌ | ✅ | ✅ |
| Modifier | ❌ | ✅ | ✅ |
| Supprimer | ❌ | ❌ | ✅ |

### Commandes

| Opération | CLIENT | GESTIONNAIRE | ADMIN |
|---|:---:|:---:|:---:|
| Créer | ✅ | ❌ | ❌ |
| Consulter | ❌ | ✅ | ✅ |
| Consulter les commandes d'un client | ❌ | ✅ | ✅ |
| Ajouter une ligne | ❌ | ✅ | ✅ |
| Modifier le statut | ❌ | ✅ | ✅ |

---

# 🗄️ Bases de données

Chaque microservice possède sa propre base de données.

```text
ms-produits
     │
     ▼
ms_produits_db
     │
     └── PostgreSQL :5433
```

```text
ms-commandes
     │
     ▼
ms_commandes_db
     │
     └── PostgreSQL :5434
```

Cette séparation respecte le principe :

> Une base de données par microservice.

`ms-commandes` ne doit donc pas accéder directement à la base de `ms-produits`.

Les informations sur les produits nécessaires aux commandes sont obtenues via OpenFeign.

---

# 🐳 Docker

Docker Compose est utilisé pour lancer l'infrastructure PostgreSQL et Keycloak.

Les services Docker sont :

```text
postgres-produits
postgres-commandes
keycloak
```

Le fichier principal est :

```text
docker-compose.yml
```

Les variables sensibles sont stockées localement dans :

```text
.env
```

Un fichier modèle est fourni :

```text
.env.example
```

Le fichier `.env` n'est pas versionné dans Git.

---

# 🧪 Tests

Les tests ont été réalisés principalement à travers l'API Gateway.

URL principale :

```text
http://localhost:8081
```

## Produits

Les scénarios suivants ont été vérifiés :

- accès sans token ;
- consultation avec CLIENT ;
- consultation avec GESTIONNAIRE ;
- consultation avec ADMIN ;
- récupération d'un produit existant ;
- récupération d'un produit inexistant ;
- création avec GESTIONNAIRE ;
- création avec CLIENT ;
- modification avec GESTIONNAIRE ;
- modification avec CLIENT ;
- suppression avec GESTIONNAIRE ;
- suppression avec ADMIN ;
- recherche par nom ;
- consultation des produits actifs ;
- pagination et tri ;
- vérification de disponibilité.

## Commandes

Les scénarios suivants ont été vérifiés :

- création d'une commande valide ;
- création avec un produit inexistant ;
- quantité négative ;
- quantité nulle ;
- vérification du stock ;
- récupération des commandes ;
- consultation des commandes d'un client ;
- ajout d'une ligne à une commande ;
- modification du statut ;
- contrôle des rôles ;
- indisponibilité de `ms-produits` ;
- transmission du JWT lors des appels OpenFeign.

## Tests automatisés

Les tests unitaires et d'intégration des microservices ont également été exécutés.

État actuel :

```text
ms-produits  : 13 tests — 0 échec
ms-commandes : 26 tests — 0 échec

Total        : 39 tests — 0 échec
```

## Codes HTTP vérifiés

```text
200 OK
201 Created
204 No Content
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
503 Service Unavailable
```

---

# 🧯 Gestion des erreurs

L'application possède une gestion explicite des erreurs.

Quelques exemples :

### Produit inexistant

```text
404 Not Found
```

### Données invalides

```text
400 Bad Request
```

### Accès sans authentification

```text
401 Unauthorized
```

### Rôle insuffisant

```text
403 Forbidden
```

### `ms-produits` indisponible

```text
503 Service Unavailable
```

Cela permet notamment à `ms-commandes` de retourner une réponse contrôlée lorsque le service des produits n'est pas disponible.

---

# 📮 Collection Postman

Une collection Postman est fournie dans :

```text
postman/
```

Fichier :

```text
INF2205-microservices.postman_collection.json
```

Elle permet de tester :

- l'authentification Keycloak ;
- les produits ;
- la pagination et le tri ;
- les commandes ;
- l'ajout de lignes ;
- les autorisations ;
- les cas d'erreur ;
- la communication entre les microservices.

Les tokens JWT sont récupérés depuis Keycloak puis utilisés dans les requêtes.

Les mots de passe et tokens JWT ne doivent pas être enregistrés dans Git.

---

# 📖 Documentation Swagger / OpenAPI

Le projet intègre **Springdoc OpenAPI** afin de documenter et tester les APIs des microservices.

Swagger UI est accessible via l'API Gateway :

```text
http://localhost:8081/swagger-ui/index.html
```

Les documentations OpenAPI des microservices sont exposées à travers le Gateway.

---

# 🚀 Installation et lancement

Les instructions détaillées d'installation, de configuration et de démarrage sont disponibles dans :

```text
START-README.md
```

Le démarrage général suit l'ordre :

```text
PostgreSQL + Keycloak
        ↓
Config Server
        ↓
Eureka Server
        ↓
ms-produits + ms-commandes
        ↓
API Gateway
        ↓
Tests Postman
```

---

# 📊 État du projet

## ✅ TP fonctionnel

Les principaux éléments demandés dans le TP sont réalisés :

- [x] `ms-produits`
- [x] `ms-commandes`
- [x] PostgreSQL
- [x] Config Server
- [x] Configuration centralisée via dépôt Git
- [x] Eureka Server
- [x] OpenFeign
- [x] API Gateway
- [x] Keycloak
- [x] OAuth2 / JWT
- [x] Gestion des rôles
- [x] Validation des données
- [x] Pagination et tri
- [x] Gestion des erreurs
- [x] Gestion de l'indisponibilité de `ms-produits`
- [x] Tests unitaires et d'intégration
- [x] Tests fonctionnels
- [x] Tests de sécurité
- [x] Collection Postman
- [x] Documentation Swagger / OpenAPI
- [x] Documentation du projet

---

# 📚 Documentation du projet

| Document | Contenu |
|---|---|
| `README.md` | Documentation générale du projet |
| `START-README.md` | Installation, configuration et lancement |
| `ms-produits/README.md` | Documentation du microservice produits |
| `ms-commandes/README.md` | Documentation du microservice commandes |
| `postman/` | Collection de tests Postman |
| `captures/` | Captures d'Eureka et Keycloak |

Le dépôt de configuration est disponible séparément :

```text
INF2205-tp-microservices-config-repo
```

---

# 👨‍💻 Projet académique

**INF2205 — Technologies du Web**

Projet réalisé dans le cadre du Master en Ingénierie Logicielle à la **Faculté des Sciences et Techniques (FST) de Bamako**.

**Architecture :**

```text
Microservices
Spring Boot
Spring Cloud
Eureka
Config Server
OpenFeign
API Gateway
Keycloak
PostgreSQL
Docker
```