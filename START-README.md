# 🚀 Installation, configuration et lancement

Ce document présente les étapes nécessaires pour installer, configurer et démarrer le projet **INF2205 — Architecture Microservices avec Spring Boot**.

L'ensemble de l'architecture repose sur Docker et Docker Compose. Les huit composants sont conteneurisés et orchestrés à partir du fichier `docker-compose.yml`.

Aucun démarrage individuel des applications Spring Boot avec Maven n'est nécessaire.

Pour découvrir l'architecture, les fonctionnalités, la sécurité et les choix technologiques, consulter le fichier `README.md`.

---

# 🐳 1. Prérequis

Avant de démarrer le projet, installer les outils suivants :

- Docker
- Docker Compose (plugin Docker)
- Git

Vérifier les installations :

```bash
docker --version
docker compose version
git --version
```

---

# 2. Récupération du projet

Cloner le dépôt GitHub :

```bash
git clone git@github.com:Sidy74/INF2205-tp-microservices.git
```

Entrer dans le répertoire du projet :

```bash
cd INF2205-tp-microservices
```

La configuration centralisée est stockée dans un dépôt GitHub distinct :

```text
INF2205-tp-microservices-config-repo
```

Le Config Server récupère automatiquement les fichiers de configuration depuis ce dépôt distant.

Il n'est donc pas nécessaire de cloner séparément le dépôt de configuration.

---

# 3. Configuration des variables d'environnement

Le projet utilise un fichier `.env` pour gérer les informations nécessaires au fonctionnement des différents composants.

Depuis la racine du projet, créer ce fichier à partir du modèle :

```bash
cp .env.example .env
```

Ouvrir le fichier :

```bash
nano .env
```

Renseigner les variables nécessaires :

```env
POSTGRES_PASSWORD=VOTRE_MOT_DE_PASSE
GITHUB_TOKEN=VOTRE_TOKEN_GITHUB
```

Description des variables :

- `POSTGRES_PASSWORD` : mot de passe utilisé par les bases de données PostgreSQL.
- `GITHUB_TOKEN` : token GitHub permettant au Config Server d'accéder au dépôt privé contenant les configurations.

Le token GitHub doit disposer des autorisations nécessaires pour lire le dépôt :

```text
INF2205-tp-microservices-config-repo
```

**Important :**
- Ne jamais publier le fichier `.env` sur GitHub.
- Le fichier `.env.example` doit uniquement servir de modèle.
- Ne jamais intégrer de véritables mots de passe, tokens ou secrets dans le dépôt.

---

# 4. Construire et démarrer toute l'architecture

Une fois les variables d'environnement configurées, exécuter la commande suivante depuis la racine du projet :

```bash
docker compose up --build -d
```

Cette commande permet :

- de construire les images Docker des applications ;
- de créer les conteneurs nécessaires ;
- de démarrer les huit composants ;
- d'exécuter l'ensemble de l'architecture en arrière-plan.

Les huit composants déployés sont :

| Composant | Rôle |
|---|---|
| Config Server | Centralisation des configurations |
| Eureka Server | Découverte des services |
| API Gateway | Routage des requêtes |
| ms-produits | Gestion du catalogue des produits |
| ms-commandes | Gestion des commandes |
| PostgreSQL Produits | Base de données des produits |
| PostgreSQL Commandes | Base de données des commandes |
| Keycloak | Authentification et autorisation |

Après le démarrage, l'ensemble de l'application fonctionne dans des conteneurs Docker.

---

# 5. Vérification des conteneurs

Pour afficher l'état des conteneurs :

```bash
docker compose ps
```

Cette commande permet de vérifier que les huit composants ont été démarrés.

Pour consulter les journaux de tous les services :

```bash
docker compose logs -f
```

Pour consulter les journaux d'un composant particulier :

```bash
docker compose logs -f ms-produits
```

```bash
docker compose logs -f ms-commandes
```

Pour quitter l'affichage des journaux sans arrêter les conteneurs, utiliser `Ctrl + C`.

---

# 6. Accès aux services

Une fois les conteneurs démarrés, les différents composants sont accessibles depuis la machine hôte à travers les ports configurés.

| Composant | Adresse |
|---|---|
| Keycloak | http://localhost:8080 |
| API Gateway | http://localhost:8081 |
| ms-produits | http://localhost:8082 |
| ms-commandes | http://localhost:8083 |
| Eureka Server | http://localhost:8761 |
| Config Server | http://localhost:8888 |

Les bases PostgreSQL utilisent les ports suivants :

| Base de données | Port |
|---|---:|
| PostgreSQL Produits | `5433` |
| PostgreSQL Commandes | `5434` |

## 6.1. API Gateway

L'API Gateway constitue le point d'entrée principal de l'application.

Les routes principales sont :

```text
/api/produits/**
/api/commandes/**
```

Le routage est effectué vers les microservices correspondants grâce à la découverte des services via Eureka.

Schéma simplifié :

```text
             Client / Postman
                    |
                    v
             API Gateway
               :8081
                    |
             +------+------+
             |             |
             v             v
        ms-produits   ms-commandes
             |             |
             v             v
        PostgreSQL    PostgreSQL
         Produits      Commandes
```

---

# 7. Vérification de Eureka

Le tableau de bord Eureka est accessible à l'adresse :

```text
http://localhost:8761
```

Dans la liste des instances enregistrées, vérifier la présence des services suivants :

```text
MS-PRODUITS
MS-COMMANDES
API-GATEWAY
```

Les services doivent apparaître avec un état :

```text
UP
```

Seuls les composants configurés comme clients Eureka apparaissent dans ce tableau de bord. Keycloak, PostgreSQL et Config Server ne sont pas nécessairement enregistrés auprès d'Eureka.

---

# 8. Configuration de Keycloak

Keycloak assure l'authentification des utilisateurs et la gestion des autorisations.

Il est démarré automatiquement avec Docker Compose.

Accéder à l'interface :

```text
http://localhost:8080
```

La configuration utilisée par le projet repose sur :

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

Après authentification, Keycloak délivre un jeton JWT utilisé pour accéder aux ressources protégées.

L'API Gateway et les microservices métiers valident les jetons afin de contrôler les accès selon les rôles des utilisateurs.

Les informations d'authentification et les secrets doivent rester confidentiels.

---

# 9. Tests avec Postman

La collection Postman du projet est disponible dans le répertoire :

```text
postman/
```

Nom de la collection :

```text
INF2205-microservices.postman_collection.json
```

L'API Gateway constitue le point d'entrée principal pour les tests :

```text
http://localhost:8081
```

La collection permet notamment de tester :

- l'authentification avec Keycloak ;
- la gestion des produits ;
- la pagination et le tri ;
- la création et la consultation des commandes ;
- l'ajout de lignes de commande ;
- la modification du statut des commandes ;
- les contrôles d'accès selon les rôles `CLIENT`, `GESTIONNAIRE` et `ADMIN` ;
- les cas d'erreur HTTP (`400`, `401`, `403`, `404` et `503`) ;
- la communication entre `ms-commandes` et `ms-produits` via OpenFeign.

Les tests fonctionnels suivent principalement ce circuit :

```text
Postman
   |
   v
API Gateway
   |
   v
Microservices
   |
   +---- ms-produits
   |
   +---- ms-commandes
```

Les mots de passe et les tokens JWT doivent être conservés dans les variables locales de Postman.

---

# 10. Vérification des bases PostgreSQL

Vérifier que les conteneurs PostgreSQL sont actifs :

```bash
docker ps
```

Pour tester la base de données `ms-produits` :

```bash
docker exec -it postgres-produits \
psql -U fst_user -d ms_produits_db \
-c "SELECT current_user, current_database();"
```

Pour tester la base de données `ms-commandes` :

```bash
docker exec -it postgres-commandes \
psql -U fst_user -d ms_commandes_db \
-c "SELECT current_user, current_database();"
```

Les commandes doivent retourner respectivement les informations des bases :

```text
fst_user | ms_produits_db
```

```text
fst_user | ms_commandes_db
```

---

# 11. Arrêter l'architecture

Pour arrêter l'ensemble des composants :

```bash
docker compose down
```

Cette commande arrête et supprime les conteneurs du projet, sans supprimer les volumes persistants.

Les données des bases PostgreSQL et de Keycloak sont ainsi conservées.

## 11.1. Réinitialisation complète

Pour arrêter les conteneurs et supprimer également les volumes :

```bash
docker compose down -v
```

**Attention :** cette commande supprime les données persistantes des bases PostgreSQL ainsi que les données persistantes de Keycloak.

À utiliser uniquement lorsqu'une réinitialisation complète de l'environnement est souhaitée.

---

# 12. Dépannage

## 12.1. Un conteneur ne démarre pas

Afficher l'état des conteneurs :

```bash
docker compose ps
```

Consulter les journaux :

```bash
docker compose logs -f NOM_DU_SERVICE
```

Remplacer `NOM_DU_SERVICE` par le nom du composant concerné.

## 12.2. Erreur de connexion à PostgreSQL

Vérifier l'état des conteneurs :

```bash
docker compose ps
```

Consulter les journaux de la base concernée :

```bash
docker compose logs -f postgres-produits
```

ou :

```bash
docker compose logs -f postgres-commandes
```

Vérifier également que `POSTGRES_PASSWORD` est correctement renseigné dans le fichier `.env`.

## 12.3. Config Server inaccessible

Vérifier son état :

```bash
docker compose ps config-server
```

Consulter ses journaux :

```bash
docker compose logs -f config-server
```

Vérifier également que :

- le `GITHUB_TOKEN` est correctement renseigné ;
- le token possède les droits nécessaires ;
- le dépôt Git de configuration est accessible ;
- la configuration du Config Server est correcte.

## 12.4. Eureka inaccessible

Vérifier son état :

```bash
docker compose ps eureka-server
```

Consulter les journaux :

```bash
docker compose logs -f eureka-server
```

## 12.5. Un microservice n'apparaît pas dans Eureka

Vérifier :

1. que le conteneur du microservice est actif ;
2. que Eureka est disponible ;
3. que la configuration du microservice est correctement récupérée ;
4. que les paramètres de découverte Eureka sont corrects.

Consulter les journaux du microservice concerné :

```bash
docker compose logs -f ms-produits
```

ou :

```bash
docker compose logs -f ms-commandes
```

## 12.6. Port déjà utilisé

Si un conteneur ne démarre pas à cause d'un port déjà utilisé, identifier le processus qui occupe ce port :

```bash
sudo lsof -i :8081
```

Remplacer `8081` par le port concerné.

---

# 13. Résumé des commandes Docker

| Opération | Commande |
|---|---|
| Préparer les variables | `cp .env.example .env` |
| Construire et démarrer | `docker compose up --build -d` |
| Vérifier les conteneurs | `docker compose ps` |
| Afficher les journaux | `docker compose logs -f` |
| Redémarrer les conteneurs | `docker compose restart` |
| Arrêter l'architecture | `docker compose down` |
| Supprimer aussi les volumes | `docker compose down -v` |

---

## 📌 Documentation complémentaire

Pour comprendre l'architecture, les fonctionnalités, la sécurité, les tests et les choix technologiques, consulter :

```text
README.md
```

Pour les instructions d'installation, de configuration et de lancement, consulter le présent document :

```text
START-README.md
```