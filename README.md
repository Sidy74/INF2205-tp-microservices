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

La sécurisation de l'architecture est prévue avec **Keycloak**.

Le projet utilise le realm :

```text
gestion-commerciale
```

Les rôles prévus sont :

```text
CLIENT
GESTIONNAIRE
ADMIN
```

Les informations sensibles telles que les mots de passe ne doivent pas être stockées dans le dépôt Git.

Le fichier local :

```text
.env
```

est ignoré par Git.

Un modèle :

```text
.env.example
```

est fourni afin de faciliter la configuration du projet.

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
- [ ] API Gateway
- [ ] Configuration de Keycloak
- [ ] Sécurisation des endpoints
- [ ] Tests Postman complets

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

La communication entre `ms-commandes` et `ms-produits` a été validée avec **OpenFeign + Eureka**.

---

## Documentation de démarrage

Pour installer et lancer le projet étape par étape, consulter :

```text
START-README.md
```

Ce fichier contient notamment :

- les prérequis ;
- le clonage du projet ;
- la configuration du fichier `.env` ;
- le démarrage de PostgreSQL ;
- le démarrage du Config Server ;
- le démarrage d'Eureka ;
- le démarrage des microservices ;
- la vérification de l'enregistrement dans Eureka ;
- les commandes de test ;
- l'arrêt de l'application.

---

## Statut du projet

🚧 **TP en cours de réalisation**

Les microservices principaux, la communication inter-services, la découverte de services, la configuration centralisée et les bases PostgreSQL sont fonctionnels.

Les prochaines étapes sont :

1. Mise en place de l'API Gateway.
2. Configuration de Keycloak.
3. Sécurisation des endpoints.
4. Tests complets de l'architecture.
5. Finalisation de la documentation.