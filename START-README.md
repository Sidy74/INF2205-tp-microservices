# 🚀 Installation, configuration et lancement

Ce document explique comment installer, configurer et lancer le projet **INF2205 — Architecture Microservices avec Spring Boot**.

Deux modes de lancement sont proposés :

1. 🐳 **Mode entièrement Docker (recommandé)** : toute l'architecture est exécutée dans des conteneurs Docker.
2. ⚙️ **Mode hybride** : Docker est utilisé pour PostgreSQL et Keycloak, tandis que les applications Spring Boot sont lancées individuellement avec Maven.

Pour la présentation générale, l'architecture, les fonctionnalités, la sécurité et les tests, consulter le fichier `README.md`.

---

# 🐳 MODE 1 — Lancement entièrement avec Docker (recommandé)

Ce mode permet de démarrer toute l'architecture microservices dans des conteneurs Docker, sans avoir besoin de lancer individuellement les applications avec Maven.

Tous les composants sont exécutés dans Docker :

- Config Server
- Eureka Server
- API Gateway
- Microservice Produits
- Microservice Commandes
- PostgreSQL Produits
- PostgreSQL Commandes
- Keycloak

## 1.1. Prérequis

Installer uniquement :

- Docker
- Docker Compose

Vérifier les installations :

```bash
docker --version
docker compose version
```

## 1.2. Préparer les variables d'environnement

Depuis la racine du projet :

```bash
cp .env.example .env
```

Ouvrir le fichier :

```bash
nano .env
```

Configurer les variables nécessaires :

```env
POSTGRES_PASSWORD=VOTRE_MOT_DE_PASSE
GITHUB_TOKEN=VOTRE_TOKEN_GITHUB
```

- `POSTGRES_PASSWORD` : mot de passe utilisé par les bases PostgreSQL.
- `GITHUB_TOKEN` : token GitHub permettant au Config Server d'accéder au dépôt privé de configuration.

Le token GitHub doit disposer des droits nécessaires pour lire le contenu du dépôt :

```text
INF2205-tp-microservices-config-repo
```

Ne jamais publier le fichier `.env` sur GitHub.

Le fichier `.env.example` doit uniquement servir de modèle et ne doit contenir aucune valeur secrète réelle.

## 1.3. Construire et démarrer toute l'architecture

Depuis la racine du projet :

```bash
docker compose up --build -d
```

Cette commande construit les images Docker des applications et démarre tous les services définis dans le fichier `docker-compose.yml`.

L'ensemble de l'architecture doit être disponible sans lancer de commandes Maven individuellement.

## 1.4. Vérifier les conteneurs

Afficher l'état des conteneurs :

```bash
docker compose ps
```

Afficher les journaux de tous les services :

```bash
docker compose logs -f
```

Afficher les journaux d'un service particulier :

```bash
docker compose logs -f ms-produits
```

```bash
docker compose logs -f ms-commandes
```

## 1.5. Accéder aux services

| Composant | URL |
|---|---|
| Keycloak | http://localhost:8080 |
| API Gateway | http://localhost:8081 |
| ms-produits | http://localhost:8082 |
| ms-commandes | http://localhost:8083 |
| Eureka Server | http://localhost:8761 |
| Config Server | http://localhost:8888 |

L'API Gateway constitue le point d'entrée principal pour les requêtes de l'application.

```text
Client
   |
   v
API Gateway : 8081
   |
   v
Microservices
   |
   +---- ms-produits
   |
   +---- ms-commandes
```

## 1.6. Arrêter toute l'architecture

Depuis la racine du projet :

```bash
docker compose down
```

Cette commande arrête et supprime les conteneurs Docker sans supprimer les volumes persistants.

Pour arrêter les conteneurs et supprimer également les volumes :

```bash
docker compose down -v
```

⚠️ Cette dernière commande supprime les données persistantes des bases PostgreSQL et de Keycloak.

À utiliser uniquement lorsqu'une réinitialisation complète de l'environnement est souhaitée.

> **Remarque importante :** ce mode suppose que le `docker-compose.yml` contient bien les huit composants, leurs configurations réseau, leurs dépendances et les Dockerfiles des cinq applications Spring Boot. La commande `docker compose up --build -d` ne crée pas automatiquement ces configurations.

---

# ⚙️ MODE 2 — Lancement hybride (Docker + Maven)

Dans ce mode, Docker est utilisé uniquement pour les bases de données PostgreSQL et Keycloak.

Les applications Spring Boot sont démarrées individuellement avec Maven.

## 2.1. Prérequis

Avant de commencer, installer :

- Java 21
- Maven
- Docker
- Docker Compose
- Git
- Postman

Vérifier les installations :

```bash
java -version
mvn -version
docker --version
docker compose version
git --version
```

## 2.2. Cloner le projet

Cloner le dépôt :

```bash
git clone git@github.com:Sidy74/INF2205-tp-microservices.git
```

Entrer dans le projet :

```bash
cd INF2205-tp-microservices
```

### Dépôt de configuration

La configuration centralisée du projet est stockée dans un dépôt Git séparé :

```text
INF2205-tp-microservices-config-repo
```

Le Config Server récupère automatiquement cette configuration depuis GitHub.

Il n'est donc pas nécessaire de cloner ce dépôt séparément pour lancer l'application.

Le dépôt de configuration est séparé du dépôt principal afin de respecter le principe de centralisation de la configuration avec Spring Cloud Config Server.

## 2.3. Configuration des variables d'environnement

Le projet utilise un fichier `.env` pour les informations sensibles nécessaires à l'exécution de l'architecture.

Créer le fichier à partir du modèle :

```bash
cp .env.example .env
```

Ouvrir le fichier :

```bash
nano .env
```

Configurer les variables nécessaires :

```env
POSTGRES_PASSWORD=VOTRE_MOT_DE_PASSE
GITHUB_TOKEN=VOTRE_TOKEN_GITHUB
```

- `POSTGRES_PASSWORD` : mot de passe utilisé par les bases PostgreSQL.
- `GITHUB_TOKEN` : token GitHub permettant au Config Server d'accéder au dépôt privé de configuration.

Le token GitHub doit disposer des droits nécessaires pour lire le contenu du dépôt :

```text
INF2205-tp-microservices-config-repo
```

Ne jamais publier le fichier `.env` sur GitHub.

Le fichier `.env.example` sert uniquement de modèle et ne doit contenir aucune valeur secrète réelle.

Charger les variables d'environnement dans le terminal :

```bash
set -a
source .env
set +a
```

Vérifier que le token est bien chargé sans afficher sa valeur :

```bash
test -n "$GITHUB_TOKEN" && echo "GITHUB_TOKEN chargé"
```

Vérifier également le mot de passe PostgreSQL :

```bash
test -n "$POSTGRES_PASSWORD" && echo "POSTGRES_PASSWORD chargé"
```

Ces variables seront utilisées par les applications qui en ont besoin.

---

# 3. Démarrer PostgreSQL et Keycloak

Depuis la racine du projet, démarrer uniquement les deux bases PostgreSQL et Keycloak :

```bash
docker compose up -d postgres-produits postgres-commandes keycloak
```

Cette commande démarre :

- PostgreSQL pour `ms-produits` ;
- PostgreSQL pour `ms-commandes` ;
- Keycloak.

Vérifier les conteneurs :

```bash
docker compose ps
```

Les ports utilisés sont :

| Service | Port |
|---|---:|
| PostgreSQL produits | `5433` |
| PostgreSQL commandes | `5434` |
| Keycloak | `8080` |

Keycloak :

```text
http://localhost:8080
```

Laisser ces conteneurs en fonctionnement pendant toute l'utilisation de l'application.

---

# 4. Démarrer le Config Server

Le Config Server centralise la configuration des différents services.

La configuration est stockée dans un dépôt Git séparé :

```text
INF2205-tp-microservices-config-repo
```

Le Config Server récupère automatiquement cette configuration depuis GitHub au démarrage.

Ouvrir un nouveau terminal.

Depuis la racine du projet, charger les variables :

```bash
set -a
source .env
set +a
```

Puis :

```bash
cd config-server
```

Lancer :

```bash
mvn spring-boot:run
```

Le Config Server utilise le port :

```text
8888
```

URL :

```text
http://localhost:8888
```

### Vérification

Tester la configuration de `ms-produits` :

```bash
curl http://localhost:8888/ms-produits/default
```

Tester également :

```bash
curl http://localhost:8888/ms-commandes/default
```

Le serveur doit retourner la configuration récupérée depuis le dépôt Git de configuration.

⚠️ Le `GITHUB_TOKEN` doit être chargé dans le terminal avant le démarrage du Config Server lorsque le dépôt Git de configuration est privé.

Laisser le processus en cours d'exécution.

---

# 5. Démarrer Eureka Server

Ouvrir un nouveau terminal.

Depuis la racine du projet :

```bash
cd eureka-server
```

Lancer :

```bash
mvn spring-boot:run
```

Eureka utilise le port :

```text
8761
```

Interface :

```text
http://localhost:8761
```

Ouvrir cette adresse dans le navigateur pour vérifier que Eureka est disponible.

Laisser le processus en cours d'exécution.

---

# 6. Démarrer ms-produits

Ouvrir un nouveau terminal.

Depuis la racine du projet, charger les variables d'environnement :

```bash
set -a
source .env
set +a
```

Puis :

```bash
cd ms-produits
```

Lancer :

```bash
mvn spring-boot:run
```

Le service utilise le port :

```text
8082
```

URL :

```text
http://localhost:8082
```

`ms-produits` doit apparaître dans Eureka après son démarrage.

---

# 7. Démarrer ms-commandes

Ouvrir un nouveau terminal.

Depuis la racine du projet :

```bash
set -a
source .env
set +a
```

Puis :

```bash
cd ms-commandes
```

Lancer :

```bash
mvn spring-boot:run
```

Le service utilise le port :

```text
8083
```

URL :

```text
http://localhost:8083
```

`ms-commandes` doit apparaître dans Eureka après son démarrage.

---

# 8. Démarrer l'API Gateway

Ouvrir un nouveau terminal.

Depuis la racine du projet :

```bash
cd api-gateway
```

Lancer :

```bash
mvn spring-boot:run
```

L'API Gateway utilise le port :

```text
8081
```

URL :

```text
http://localhost:8081
```

L'API Gateway doit également apparaître dans Eureka.

---

# 9. Vérifier Eureka

Ouvrir :

```text
http://localhost:8761
```

Dans la liste des instances enregistrées, vérifier la présence de :

```text
MS-PRODUITS
MS-COMMANDES
API-GATEWAY
```

Les services doivent être enregistrés avec un état :

```text
UP
```

Une vérification rapide depuis le terminal est également possible :

```bash
curl -s http://localhost:8761/eureka/apps
```

---

# 10. Configuration de Keycloak

Keycloak est utilisé comme serveur d'authentification et d'autorisation de l'architecture.

Il est lancé automatiquement avec Docker Compose.

Accéder à :

```text
http://localhost:8080
```

Configuration utilisée par le projet :

```text
Realm  : gestion-commerciale
Client : gestion-commerciale-app
```

Les rôles utilisés sont :

```text
CLIENT
GESTIONNAIRE
ADMIN
```

Le JWT émis par Keycloak est utilisé par l'API Gateway et les microservices protégés.

Les microservices `ms-produits` et `ms-commandes` fonctionnent comme **OAuth2 Resource Server** et valident directement les JWT émis par Keycloak.

Le `clientId` utilisé lors de la création d'une commande est récupéré depuis le claim `sub` du JWT authentifié.

Les utilisateurs de test sont configurés dans Keycloak.

⚠️ Les identifiants, mots de passe et tokens JWT ne doivent pas être ajoutés au dépôt Git.

---

# 11. Postman

La collection Postman du projet est disponible dans :

```text
postman/
```

Collection :

```text
INF2205-microservices.postman_collection.json
```

L'API Gateway constitue le point d'entrée principal utilisé pour les tests :

```text
http://localhost:8081
```

La collection permet notamment de tester :

- l'authentification avec Keycloak ;
- la gestion des produits ;
- la pagination et le tri ;
- la création de commandes ;
- la consultation des commandes ;
- l'ajout de lignes à une commande ;
- la modification du statut d'une commande ;
- les contrôles d'accès selon les rôles `CLIENT`, `GESTIONNAIRE` et `ADMIN` ;
- les cas d'erreur (`401`, `403`, `404`, `400` et `503`) ;
- la communication entre `ms-commandes` et `ms-produits` via OpenFeign.

Les tests fonctionnels sont normalement exécutés à travers :

```text
Client
   ↓
API Gateway : 8081
   ↓
Microservices
```

Les mots de passe, tokens JWT et autres informations sensibles doivent rester dans les variables locales de Postman et ne doivent pas être commités dans Git.

---

# 12. Ordre de démarrage du mode hybride

Pour éviter les problèmes de découverte de services, respecter cet ordre :

```text
1. PostgreSQL + Keycloak
           ↓
2. Config Server : 8888
           ↓
3. Eureka Server : 8761
           ↓
4. ms-produits : 8082
   ms-commandes : 8083
           ↓
5. API Gateway : 8081
           ↓
6. Postman / tests
```

Cet ordre permet notamment :

- au Config Server d'être disponible avant les clients ;
- à Eureka d'être disponible avant les microservices ;
- aux microservices de s'enregistrer auprès d'Eureka ;
- à l'API Gateway de découvrir les microservices.

---

# 13. Résumé des ports

| Composant | Port |
|---|---:|
| Keycloak | `8080` |
| API Gateway | `8081` |
| ms-produits | `8082` |
| ms-commandes | `8083` |
| Config Server | `8888` |
| Eureka Server | `8761` |
| PostgreSQL produits | `5433` |
| PostgreSQL commandes | `5434` |

---

# 14. Vérifier PostgreSQL

Vérifier que les conteneurs PostgreSQL sont actifs :

```bash
docker ps
```

Charger les variables si nécessaire :

```bash
set -a
source .env
set +a
```

Tester la base `ms-produits` :

```bash
docker exec -e PGPASSWORD="$POSTGRES_PASSWORD" -it postgres-produits \
psql -U fst_user -d ms_produits_db \
-c "SELECT current_user, current_database();"
```

Tester la base `ms-commandes` :

```bash
docker exec -e PGPASSWORD="$POSTGRES_PASSWORD" -it postgres-commandes \
psql -U fst_user -d ms_commandes_db \
-c "SELECT current_user, current_database();"
```

Les commandes doivent retourner respectivement :

```text
fst_user | ms_produits_db
```

et :

```text
fst_user | ms_commandes_db
```

---

# 15. Arrêter les applications Spring Boot

Dans chaque terminal où une application Spring Boot est lancée :

```text
Ctrl + C
```

Arrêter les services dans l'ordre inverse du démarrage :

```text
API Gateway
    ↓
ms-commandes
    ↓
ms-produits
    ↓
Eureka Server
    ↓
Config Server
```

---

# 16. Arrêter Docker

Depuis la racine du projet :

```bash
docker compose down
```

Cette commande arrête les conteneurs Docker du projet, notamment :

- PostgreSQL produits ;
- PostgreSQL commandes ;
- Keycloak.

Les volumes Docker sont conservés.

---

# 17. Supprimer les volumes Docker

Pour arrêter les conteneurs et supprimer également les volumes :

```bash
docker compose down -v
```

⚠️ Cette commande supprime les volumes PostgreSQL et donc les données stockées dans les bases. Elle supprime également les données persistantes de Keycloak.

À utiliser uniquement si une réinitialisation complète de l'environnement est souhaitée.

---

# 18. Dépannage

## Port déjà utilisé

Si une application ne démarre pas parce qu'un port est déjà utilisé :

```bash
sudo lsof -i :8082
```

Remplacer `8082` par le port concerné.

Par exemple :

```bash
sudo lsof -i :8081
sudo lsof -i :8083
sudo lsof -i :8761
sudo lsof -i :8888
```

## Config Server inaccessible

Vérifier que le Config Server est lancé :

```bash
curl http://localhost:8888/ms-produits/default
```

Si aucune réponse n'est obtenue, démarrer :

```bash
cd config-server
mvn spring-boot:run
```

## Eureka inaccessible

Vérifier :

```bash
curl http://localhost:8761/eureka/apps
```

Si Eureka ne répond pas :

```bash
cd eureka-server
mvn spring-boot:run
```

## Un microservice n'apparaît pas dans Eureka

Vérifier que :

1. Config Server est démarré ;
2. Eureka est démarré ;
3. le microservice est démarré après Eureka ;
4. le microservice utilise la bonne configuration.

Si nécessaire, redémarrer le microservice après Eureka.

## Erreur de connexion PostgreSQL

Vérifier les conteneurs :

```bash
docker ps
```

Vérifier que les variables d'environnement sont chargées :

```bash
set -a
source .env
set +a
```

Vérifier que `POSTGRES_PASSWORD` est bien défini, sans afficher sa valeur :

```bash
test -n "$POSTGRES_PASSWORD" && echo "POSTGRES_PASSWORD chargé"
```

Ensuite, redémarrer le microservice concerné.

## Problème après changement de réseau

Si la machine change de réseau Wi-Fi, Eureka peut conserver temporairement une ancienne adresse IP d'une instance.

Dans ce cas, redémarrer les services dans cet ordre :

```text
Config Server
    ↓
Eureka
    ↓
ms-produits
    ↓
ms-commandes
    ↓
API Gateway
```

Cela permet aux instances de se réenregistrer avec leur nouvelle adresse.

---

# 19. Commandes rapides

## Mode entièrement Docker

### Préparer l'environnement

```bash
cp .env.example .env
```

Configurer les variables dans `.env`, puis :

```bash
docker compose up --build -d
```

### Vérifier les conteneurs

```bash
docker compose ps
```

### Consulter les journaux

```bash
docker compose logs -f
```

### Arrêter toute l'architecture

```bash
docker compose down
```

## Mode hybride

### Préparer l'environnement

Depuis la racine du projet :

```bash
set -a
source .env
set +a
```

### Démarrer l'infrastructure

```bash
docker compose up -d postgres-produits postgres-commandes keycloak
```

### Vérifier Docker

```bash
docker compose ps
```

### Config Server

```bash
cd config-server
mvn spring-boot:run
```

### Eureka

```bash
cd eureka-server
mvn spring-boot:run
```

### ms-produits

```bash
cd ms-produits
mvn spring-boot:run
```

### ms-commandes

```bash
cd ms-commandes
mvn spring-boot:run
```

### API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

### Arrêter Docker

```bash
docker compose down
```

---

# 20. URLs principales

| Service | Adresse |
|---|---|
| Keycloak | http://localhost:8080 |
| API Gateway | http://localhost:8081 |
| ms-produits | http://localhost:8082 |
| ms-commandes | http://localhost:8083 |
| Eureka | http://localhost:8761 |
| Config Server | http://localhost:8888 |

---

## 📌 Documentation complémentaire

Pour comprendre l'architecture, les fonctionnalités, la sécurité, les tests et les choix techniques :

```text
README.md
```

Pour l'installation et le lancement :

```text
START-README.md
```