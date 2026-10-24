# INF2205 — TP Microservices

## Mise en place d'une architecture microservices avec Spring Boot

Ce dépôt contient le travail pratique du cours **INF2205 — Technologies du Web** portant sur la conception et la mise en œuvre d'une architecture microservices avec Spring Boot et Spring Cloud.

## Architecture

- **ms-produits** — gestion du catalogue des produits
- **ms-commandes** — gestion des commandes
- **Eureka Server** — découverte et enregistrement des services
- **OpenFeign** — communication entre les microservices
- **Config Server** — centralisation de la configuration
- **API Gateway** — point d'entrée unique de l'application
- **Keycloak** — authentification et gestion des rôles

## Technologies

- Java
- Spring Boot
- Spring Cloud
- Spring Data JPA
- PostgreSQL
- Eureka
- OpenFeign
- Spring Cloud Gateway
- Spring Cloud Config
- Keycloak
- Maven
- Docker
- Postman

## Ports

| Application | Port |
|---|---:|
| Keycloak | 8080 |
| API Gateway | 8081 |
| ms-produits | 8082 |
| ms-commandes | 8083 |
| Eureka Server | 8761 |
| Config Server | 8888 |

## Statut

🚧 TP en cours de réalisation.