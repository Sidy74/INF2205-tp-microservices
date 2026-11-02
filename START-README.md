# 🚀 Installation, configuration et lancement

Ce document explique comment installer, configurer et lancer le projet **INF2205 — Architecture Microservices avec Spring Boot**.

Pour la présentation générale, l'architecture, les fonctionnalités, la sécurité et les tests, consulter le fichier :

```text
README.md
```

---

# 1. Prérequis

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

---

# 2. Cloner le projet

Cloner le dépôt :

```bash
git clone git@github.com:Sidy74/INF2205-tp-microservices.git
```

Entrer dans le projet :

```bash
cd INF2205-tp-microservices
```

---

### Dépôt de configuration

La configuration centralisée du projet est stockée dans un dépôt Git séparé :

```text
INF2205-tp-microservices-config-repo
```

Le Config Server récupère automatiquement cette configuration depuis GitHub.

Il n'est donc pas nécessaire de cloner ce dépôt séparément pour lancer l'application.

> ℹ️ Le dépôt de configuration est séparé du dépôt principal afin de respecter le principe de centralisation de la configuration avec Spring Cloud Config Server.

# 3. Configuration des variables d'environnement

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

Le token GitHub doit disposer des droits nécessaires pour lire le contenu du dépôt de configuration :

```text
INF2205-tp-microservices-config-repo
```

> ⚠️ Ne jamais publier le fichier `.env` sur GitHub. Il contient des informations sensibles.

Le fichier `.env.example` sert uniquement de modèle et ne doit contenir aucune valeur secrète réelle.

Avant de démarrer le Config Server, charger les variables dans le terminal :

```bash
export $(grep -v '^#' .env | xargs)
```

Vérifier que le token est bien chargé sans afficher sa valeur :

```bash
test -n "$GITHUB_TOKEN" && echo "GITHUB_TOKEN chargé"
```

Le Config Server utilise ensuite cette variable pour s'authentifier auprès du dépôt Git de configuration.

# 4. Démarrer PostgreSQL et Keycloak

Depuis la racine du projet :

```bash
docker compose up -d
```

Cette commande démarre :

- PostgreSQL pour `ms-produits`
- PostgreSQL pour `ms-commandes`
- Keycloak

Vérifier les conteneurs :

```bash
docker ps
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

---

# 5. Démarrer le Config Server

Le Config Server centralise la configuration des différents services.

La configuration est stockée dans un **dépôt Git séparé** :

```text
INF2205-tp-microservices-config-repo
```

Le Config Server récupère automatiquement cette configuration depuis GitHub au démarrage.

Ouvrir un nouveau terminal.

Depuis la racine du projet :

```bash
export $(grep -v '^#' .env | xargs)
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

Le serveur doit retourner la configuration récupérée depuis le dépôt Git de configuration.

Tester également :

```bash
curl http://localhost:8888/ms-commandes/default
```

Si les réponses sont retournées correctement, le Config Server est opérationnel.

> ⚠️ Le `GITHUB_TOKEN` doit être chargé dans le terminal avant le démarrage du Config Server lorsque le dépôt Git de configuration est privé.

Laisser le processus en cours d'exécution.

# 6. Démarrer Eureka Server

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

# 7. Démarrer `ms-produits`

Ouvrir un nouveau terminal.

Depuis la racine du projet, charger les variables d'environnement :

```bash
export $(grep -v '^#' .env | xargs)
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

# 8. Démarrer `ms-commandes`

Ouvrir un nouveau terminal.

Depuis la racine du projet :

```bash
export $(grep -v '^#' .env | xargs)
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

# 9. Démarrer l'API Gateway

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

# 10. Vérifier Eureka

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

# 11. Configuration de Keycloak

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

> ⚠️ Les identifiants, mots de passe et tokens JWT ne doivent pas être ajoutés au dépôt Git.

# 12. Postman

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

# 13. Ordre de démarrage

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

# 14. Résumé des ports

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

# 15. Vérifier PostgreSQL

Vérifier que les conteneurs PostgreSQL sont actifs :

```bash id="g8q2pw"
docker ps
```

Tester la base `ms-produits` :

```bash id="j1n7qc"
docker exec -e PGPASSWORD="$POSTGRES_PASSWORD" -it postgres-produits \
psql -U fst_user -d ms_produits_db \
-c "SELECT current_user, current_database();"
```

Tester la base `ms-commandes` :

```bash id="c4v8rz"
docker exec -e PGPASSWORD="$POSTGRES_PASSWORD" -it postgres-commandes \
psql -U fst_user -d ms_commandes_db \
-c "SELECT current_user, current_database();"
```

Si la variable n'est pas encore chargée dans le terminal :

```bash id="k9p3sx"
export $(grep -v '^#' .env | xargs)
```

Les commandes doivent retourner respectivement :

```text
fst_user | ms_produits_db
```

et :

```text
fst_user | ms_commandes_db
```

# 16. Arrêter les applications Spring Boot

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

# 17. Arrêter Docker

Depuis la racine du projet :

```bash
docker compose down
```

Cette commande arrête :

- PostgreSQL produits ;
- PostgreSQL commandes ;
- Keycloak.

Les volumes Docker sont conservés.

---

# 18. Supprimer les volumes Docker

Pour arrêter les conteneurs et supprimer également les volumes :

```bash
docker compose down -v
```

> ⚠️ Cette commande supprime les volumes PostgreSQL et donc les données stockées dans les bases.

À utiliser uniquement si une réinitialisation complète des bases est souhaitée.

---

# 19. Dépannage

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

---

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

---

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

---

## Un microservice n'apparaît pas dans Eureka

Vérifier que :

1. Config Server est démarré ;
2. Eureka est démarré ;
3. le microservice est démarré après Eureka ;
4. le microservice utilise la bonne configuration.

Si nécessaire, redémarrer le microservice après Eureka.

---

## Erreur de connexion PostgreSQL

Vérifier les conteneurs :

```bash
docker ps
```

Vérifier que les variables d'environnement sont chargées :

```bash
export $(grep -v '^#' .env | xargs)
```

Vérifier que `POSTGRES_PASSWORD` est bien défini, sans afficher sa valeur :

```bash
test -n "$POSTGRES_PASSWORD" && echo "POSTGRES_PASSWORD chargé"
```

Ensuite redémarrer le microservice concerné.

---

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

# 20. Commandes rapides

### Préparer l'environnement

Depuis la racine du projet :

```bash id="v9x1cq"
export $(grep -v '^#' .env | xargs)
```

### Démarrer l'infrastructure

```bash id="z7k2mw"
docker compose up -d
```

### Vérifier Docker

```bash id="p4n8rs"
docker ps
```

### Config Server

```bash id="s6t3jd"
cd config-server
mvn spring-boot:run
```

### Eureka

```bash id="a2q5yk"
cd eureka-server
mvn spring-boot:run
```

### ms-produits

```bash id="h8c4nv"
cd ms-produits
mvn spring-boot:run
```

### ms-commandes

```bash id="j3r7xp"
cd ms-commandes
mvn spring-boot:run
```

### API Gateway

```bash id="w5d9kf"
cd api-gateway
mvn spring-boot:run
```

### Arrêter Docker

```bash id="e6m2qs"
docker compose down
```

# 21. URLs principales

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