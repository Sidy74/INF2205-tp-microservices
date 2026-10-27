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

# 3. Configuration des variables d'environnement

Le projet utilise un fichier `.env` pour les informations sensibles.

Créer le fichier à partir du modèle :

```bash
cp .env.example .env
```

Ouvrir le fichier :

```bash
nano .env
```

Configurer le mot de passe PostgreSQL :

```env
POSTGRES_PASSWORD=VOTRE_MOT_DE_PASSE
```

> ⚠️ Le fichier `.env` est ignoré par Git et ne doit pas être publié sur GitHub.

Le fichier `.env.example` peut être utilisé comme modèle pour les autres développeurs.

---

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

Ouvrir un nouveau terminal.

Depuis la racine du projet :

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

```bash
curl http://localhost:8888/ms-produits/default
```

Le serveur doit retourner la configuration de `ms-produits`.

Laisser le processus en cours d'exécution.

---

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

Keycloak est lancé automatiquement avec Docker Compose.

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

Les utilisateurs de test sont configurés dans Keycloak.

> ⚠️ Les identifiants et mots de passe ne doivent pas être ajoutés au dépôt Git public.

---

# 12. Postman

La collection Postman du projet est disponible dans :

```text
postman/
```

Collection :

```text
INF2205-microservices.postman_collection.json
```

L'API Gateway constitue le point d'entrée utilisé pour les tests :

```text
http://localhost:8081
```

La collection permet notamment de tester l'authentification et les différents endpoints du projet.

Les mots de passe doivent rester dans les variables locales de Postman et ne doivent pas être commités dans Git.

---

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

Vérifier les conteneurs :

```bash
docker ps
```

Tester la base `ms-produits` :

```bash
docker exec -e PGPASSWORD='VOTRE_MOT_DE_PASSE' -it postgres-produits \
psql -U fst_user -d ms_produits_db \
-c "SELECT current_user, current_database();"
```

Tester la base `ms-commandes` :

```bash
docker exec -e PGPASSWORD='VOTRE_MOT_DE_PASSE' -it postgres-commandes \
psql -U fst_user -d ms_commandes_db \
-c "SELECT current_user, current_database();"
```

---

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

Vérifier le fichier :

```bash
cat .env
```

Puis charger les variables :

```bash
export $(grep -v '^#' .env | xargs)
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

### Démarrer l'infrastructure

```bash
docker compose up -d
```

### Vérifier Docker

```bash
docker ps
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
export $(grep -v '^#' .env | xargs)
cd ms-produits
mvn spring-boot:run
```

### ms-commandes

```bash
export $(grep -v '^#' .env | xargs)
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