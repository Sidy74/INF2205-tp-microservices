# INF2205 — TP Microservices

## Mise en place d'une architecture microservices avec Spring Boot

Ce dépôt contient le travail pratique du cours **INF2205 — Technologies du Web** portant sur la conception et la mise en œuvre d'une architecture microservices avec **Spring Boot** et **Spring Cloud**.

L'objectif du projet est de mettre en place une architecture composée de plusieurs microservices indépendants communiquant entre eux, avec découverte de services, configuration centralisée, API Gateway et sécurisation avec Keycloak.

---

## Architecture

L'architecture du projet est composée des éléments suivants :

- **ms-produits** — gestion du catalogue des produits
- **ms-commandes** — gestion des commandes
- **Eureka Server** — découverte et enregistrement des services
- **OpenFeign** — communication entre les microservices
- **Config Server** — centralisation de la configuration
- **API Gateway** — point d'entrée unique de l'application
- **Keycloak** — authentification et gestion des rôles
- **PostgreSQL** — bases de données des microservices

### Communication entre les services

```text
                         ┌─────────────────┐
                         │    Keycloak     │
                         │      :8080      │
                         └─────────────────┘
                                  │
                                  │ Authentification
                                  ▼
┌───────────────┐        ┌─────────────────┐
│    Client     │ ─────► │   API Gateway   │
│ Postman / Web │        │      :8081      │
└───────────────┘        └────────┬────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    ▼                           ▼
          ┌─────────────────┐         ┌─────────────────┐
          │  ms-produits    │         │  ms-commandes   │
          │      :8082      │◄────────│      :8083      │
          └────────┬────────┘ OpenFeign └────────┬──────┘
                   │                            │
                   ▼                            ▼
          ┌─────────────────┐         ┌─────────────────┐
          │ PostgreSQL      │         │ PostgreSQL      │
          │ produits :5433  │         │ commandes :5434 │
          └─────────────────┘         └─────────────────┘

                     ┌─────────────────┐
                     │ Eureka Server   │
                     │      :8761      │
                     └─────────────────┘

                     ┌─────────────────┐
                     │ Config Server   │
                     │      :8888      │
                     └─────────────────┘
```

> `ms-commandes` ne communique pas directement avec la base de données de `ms-produits`. Les informations sur les produits sont récupérées via **OpenFeign**, en utilisant **Eureka** pour la découverte du service.

---

## Technologies

- **Java 21**
- **Spring Boot 4.1.2**
- **Spring Cloud 2025.1.4**
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Cloud Config
- Spring Cloud Netflix Eureka
- Spring Cloud OpenFeign
- Spring Cloud Gateway
- PostgreSQL 17
- Keycloak
- Maven
- Docker
- Docker Compose
- Postman
- Git / GitHub

---

## Structure du projet

```text
INF2205-tp-microservices/
│
├── README.md
├── START-README.md
├── .gitignore
├── .env.example
├── docker-compose.yml
│
├── ms-produits/
│   └── ...
│
├── ms-commandes/
│   └── ...
│
├── eureka-server/
│   └── ...
│
├── config-server/
│   └── ...
│
├── config-repo/
│   ├── application.yml
│   ├── ms-produits.yml
│   ├── ms-commandes.yml
│   ├── api-gateway.yml
│   └── eureka-server.yml
│
└── postman/
    └── INF2205-microservices.postman_collection.json
```

---

## Ports

| Application | Port |
|---|---:|
| Keycloak | `8080` |
| API Gateway | `8081` |
| ms-produits | `8082` |
| ms-commandes | `8083` |
| Eureka Server | `8761` |
| Config Server | `8888` |
| PostgreSQL produits | `5433` |
| PostgreSQL commandes | `5434` |

---

## PostgreSQL avec Docker

Le projet utilise **Docker Compose** pour lancer deux bases PostgreSQL indépendantes.

| Service | Base de données | Port hôte | Port PostgreSQL |
|---|---|---:|---:|
| PostgreSQL produits | `ms_produits_db` | `5433` | `5432` |
| PostgreSQL commandes | `ms_commandes_db` | `5434` | `5432` |

Chaque microservice possède ainsi sa propre base de données.

### Démarrer PostgreSQL

Depuis la racine du projet :

```bash
docker compose up -d
```

### Vérifier les conteneurs

```bash
docker ps
```

### Arrêter PostgreSQL

```bash
docker compose down
```

Les données sont conservées dans les volumes Docker :

```text
postgres-produits-data
postgres-commandes-data
```

---

## Configuration

Les configurations communes et spécifiques aux microservices sont centralisées dans le dossier :

```text
config-repo/
```

Fichiers principaux :

```text
config-repo/
├── application.yml
├── ms-produits.yml
├── ms-commandes.yml
├── api-gateway.yml
└── eureka-server.yml
```

Le **Config Server** utilise ce dépôt de configuration afin de fournir les paramètres nécessaires aux différents services.

---

## Sécurité

La sécurisation de l'architecture est mise en place avec **Keycloak** et **OAuth2/JWT**.

### Realm

```text
gestion-commerciale
```

### Client

```text
gestion-commerciale-app
```

### Rôles

- `CLIENT`
- `GESTIONNAIRE`
- `ADMIN`

### Politique d'accès

| Fonctionnalité | CLIENT | GESTIONNAIRE | ADMIN |
|---|:---:|:---:|:---:|
| Consulter les produits | ✅ | ✅ | ✅ |
| Créer un produit | ❌ | ✅ | ✅ |
| Modifier un produit | ❌ | ✅ | ✅ |
| Supprimer un produit | ❌ | ❌ | ✅ |
| Créer une commande | ✅ | ❌ | ❌ |
| Consulter les commandes | ❌ | ✅ | ✅ |

L'API Gateway valide les JWT émis par Keycloak et extrait les rôles depuis `realm_access.roles`.

Les informations sensibles, notamment les mots de passe et secrets, ne sont pas stockées dans le dépôt Git.

Le fichier local :

```text
.env
```

est ignoré par Git.

Un modèle :

```text
.env.example
```

est fourni pour faciliter la configuration du projet.

---

## Fonctionnalités réalisées

- [x] Création du microservice `ms-produits`
- [x] Création du microservice `ms-commandes`
- [x] Configuration PostgreSQL avec Docker Compose
- [x] Configuration centralisée avec Spring Cloud Config
- [x] Mise en place d'Eureka Server
- [x] Enregistrement des microservices dans Eureka
- [x] Communication entre `ms-commandes` et `ms-produits` avec OpenFeign
- [x] Gestion des produits
- [x] Vérification de la disponibilité des produits
- [x] Création des commandes
- [x] Consultation des commandes
- [x] Modification du statut des commandes
- [x] Mise en place de l'API Gateway
- [x] Configuration de Keycloak
- [x] Sécurisation des endpoints du Gateway avec OAuth2/JWT
- [x] Gestion des rôles `CLIENT`, `GESTIONNAIRE` et `ADMIN`
- [x] Contrôle des autorisations selon les rôles
- [x] Tests des accès autorisés et refusés

---

## Tests réalisés

### Produits

Création d'un produit :

```http
POST /api/produits
```

Consultation des produits :

```http
GET /api/produits
```

Modification d'un produit :

```http
PUT /api/produits/{id}
```

Suppression d'un produit :

```http
DELETE /api/produits/{id}
```

### Commandes

Création d'une commande :

```http
POST /api/commandes
```

Consultation des commandes :

```http
GET /api/commandes
```

Consultation d'une commande :

```http
GET /api/commandes/{id}
```

Modification du statut :

```http
PATCH /api/commandes/{id}/statut?statut=VALIDEE
```

### Tests de sécurité

Les principaux scénarios de sécurité ont été validés :

- Requête sans token → `401 Unauthorized`
- CLIENT → consultation des produits → `200 OK`
- CLIENT → suppression d'un produit → `403 Forbidden`
- CLIENT → création d'une commande → `201 Created`
- CLIENT → consultation des commandes → `403 Forbidden`
- GESTIONNAIRE → consultation des commandes → `200 OK`
- GESTIONNAIRE → création d'un produit → `201 Created`
- GESTIONNAIRE → modification d'un produit → `200 OK`
- GESTIONNAIRE → suppression d'un produit → `403 Forbidden`
- ADMIN → suppression d'un produit → `204 No Content`

La communication entre `ms-commandes` et `ms-produits` a été validée avec **OpenFeign + Eureka**.

---

## Statut du projet

🚧 **TP en cours de réalisation**

Les éléments suivants sont actuellement fonctionnels :

- les microservices `ms-produits` et `ms-commandes` ;
- les bases PostgreSQL indépendantes ;
- la configuration centralisée avec Config Server ;
- la découverte de services avec Eureka ;
- la communication inter-services avec OpenFeign ;
- l'API Gateway ;
- l'authentification avec Keycloak ;
- la gestion des rôles et autorisations au niveau du Gateway.

### Prochaines étapes

1. Sécurisation directe de `ms-produits`.
2. Sécurisation directe de `ms-commandes`.
3. Tests complets de l'architecture.
4. Finalisation de la documentation et des livrables du TP.