# 🚀 Installation et lancement du projet

Ce document explique comment installer, configurer et lancer l'architecture microservices du TP **INF2205 — Technologies du Web**.

---

# 1. Prérequis

Installer les logiciels suivants :

- **Java 21**
- **Maven**
- **Docker**
- **Docker Compose**
- **Git**

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

Depuis le terminal :

```bash
git clone git@github.com:Sidy74/INF2205-tp-microservices.git
```

Entrer dans le projet :

```bash
cd INF2205-tp-microservices
```

---

# 3. Configurer les variables d'environnement

Le projet utilise un fichier `.env` pour les informations sensibles, notamment le mot de passe PostgreSQL.

Créer le fichier `.env` à partir du modèle fourni :

```bash
cp .env.example .env
```

Ouvrir le fichier :

```bash
nano .env
```

Renseigner le mot de passe PostgreSQL :

```env
POSTGRES_PASSWORD=VOTRE_MOT_DE_PASSE
```

> ⚠️ Le fichier `.env` contient des informations sensibles et n'est pas versionné sur GitHub.

---

# 4. Démarrer les bases PostgreSQL

Depuis la **racine du projet** :

```bash
docker compose up -d
```

Vérifier que les conteneurs sont démarrés :

```bash
docker ps
```

Les deux conteneurs PostgreSQL doivent être présents :

| Conteneur | Base de données | Port |
|---|---|---:|
| `postgres-produits` | `ms_produits_db` | `5433` |
| `postgres-commandes` | `ms_commandes_db` | `5434` |

Les ports `5433` et `5434` correspondent aux ports exposés sur la machine hôte.

PostgreSQL utilise le port `5432` à l'intérieur de chaque conteneur.

---

# 5. Démarrer le Config Server

Ouvrir un **nouveau terminal**.

Depuis le dossier du projet :

```bash
cd INF2205-tp-microservices/config-server
```

Lancer :

```bash
mvn spring-boot:run
```

Le Config Server doit être accessible sur :

```text
http://localhost:8888
```

### Vérification

Tester par exemple :

```bash
curl http://localhost:8888/ms-produits/default
```

Le serveur doit retourner la configuration de `ms-produits`.

Laisser ce terminal ouvert.

---

# 6. Démarrer Eureka Server

Ouvrir un **deuxième nouveau terminal**.

Depuis le dossier du projet :

```bash
cd INF2205-tp-microservices/eureka-server
```

Lancer :

```bash
mvn spring-boot:run
```

Eureka doit être accessible sur :

```text
http://localhost:8761
```

Ouvrir cette adresse dans le navigateur.

Laisser ce terminal ouvert.

---

# 7. Démarrer `ms-produits`

Ouvrir un **troisième nouveau terminal**.

Depuis la racine du projet :

```bash
cd INF2205-tp-microservices
```

Charger les variables d'environnement :

```bash
export $(grep -v '^#' .env | xargs)
```

Puis entrer dans le microservice :

```bash
cd ms-produits
```

Lancer l'application :

```bash
mvn spring-boot:run
```

Le microservice doit démarrer sur :

```text
http://localhost:8082
```

Il doit également apparaître dans Eureka sous :

```text
MS-PRODUITS
```

Laisser ce terminal ouvert.

---

# 8. Démarrer `ms-commandes`

Ouvrir un **quatrième nouveau terminal**.

Depuis la racine du projet :

```bash
cd INF2205-tp-microservices
```

Charger les variables d'environnement :

```bash
export $(grep -v '^#' .env | xargs)
```

Puis entrer dans le microservice :

```bash
cd ms-commandes
```

Lancer :

```bash
mvn spring-boot:run
```

Le microservice doit démarrer sur :

```text
http://localhost:8083
```

Il doit également apparaître dans Eureka sous :

```text
MS-COMMANDES
```

Laisser ce terminal ouvert.

---

# 9. Vérifier Eureka

Ouvrir :

```text
http://localhost:8761
```

Dans la section :

```text
Instances currently registered with Eureka
```

Vérifier la présence de :

```text
MS-PRODUITS
MS-COMMANDES
```

Les deux services doivent être en état :

```text
UP
```

---

# 10. Tester `ms-produits`

## 10.1 Lister les produits

```bash
curl http://localhost:8082/api/produits
```

---

## 10.2 Créer un produit

```bash
curl -X POST http://localhost:8082/api/produits \
  -H "Content-Type: application/json" \
  -d '{
    "reference": "P001",
    "nom": "Ordinateur portable",
    "description": "Ordinateur de test",
    "prix": 500000,
    "quantiteStock": 10,
    "actif": true
  }'
```

La réponse doit contenir notamment :

```json
{
  "id": 1,
  "reference": "P001",
  "nom": "Ordinateur portable",
  "prix": 500000,
  "quantiteStock": 10,
  "actif": true
}
```

> L'identifiant peut être différent selon l'état de la base de données.

---

## 10.3 Consulter un produit

```bash
curl http://localhost:8082/api/produits/1
```

Remplacer `1` par l'identifiant réel du produit.

---

# 11. Tester `ms-commandes`

## 11.1 Créer une commande

Une commande doit contenir au moins une ligne.

Exemple :

```bash
curl -X POST http://localhost:8083/api/commandes \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "lignes": [
      {
        "produitId": 1,
        "quantite": 2
      }
    ]
  }'
```

Lors de la création, `ms-commandes` :

1. récupère le produit auprès de `ms-produits` ;
2. utilise **OpenFeign** pour effectuer l'appel ;
3. utilise **Eureka** pour découvrir `ms-produits` ;
4. vérifie que le produit est actif ;
5. vérifie la disponibilité du stock ;
6. récupère le prix du produit ;
7. calcule le montant de la commande ;
8. enregistre la commande dans sa propre base PostgreSQL.

---

# 12. Consulter les commandes

Lister toutes les commandes :

```bash
curl http://localhost:8083/api/commandes
```

---

# 13. Consulter une commande

Utiliser l'identifiant de la commande :

```bash
curl http://localhost:8083/api/commandes/1
```

Remplacer `1` par l'identifiant réel de la commande.

---

# 14. Modifier le statut d'une commande

Les statuts disponibles sont :

```text
EN_ATTENTE
VALIDEE
EXPEDIEE
LIVREE
ANNULEE
```

Exemple pour passer une commande à `VALIDEE` :

```bash
curl -X PATCH "http://localhost:8083/api/commandes/1/statut?statut=VALIDEE"
```

Vérifier ensuite la commande :

```bash
curl http://localhost:8083/api/commandes/1
```

La réponse doit contenir :

```json
{
  "statut": "VALIDEE"
}
```

---

# 15. Tester la communication OpenFeign

La communication entre les deux microservices est réalisée comme suit :

```text
ms-commandes
     │
     │ OpenFeign
     ▼
   Eureka
     │
     ▼
ms-produits
```

`ms-commandes` ne se connecte donc pas directement à la base de données de `ms-produits`.

Pour vérifier cette communication, créer une commande :

```bash
curl -X POST http://localhost:8083/api/commandes \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "lignes": [
      {
        "produitId": 1,
        "quantite": 2
      }
    ]
  }'
```

Si la commande est créée correctement, la communication entre `ms-commandes`, Eureka et `ms-produits` fonctionne.

---

# 16. Vérifier les bases PostgreSQL

Vérifier les conteneurs :

```bash
docker ps
```

Pour tester la base `ms-commandes` :

```bash
docker exec -e PGPASSWORD='VOTRE_MOT_DE_PASSE' -it postgres-commandes \
psql -U fst_user -d ms_commandes_db \
-c "SELECT current_user, current_database();"
```

Pour tester la base `ms-produits` :

```bash
docker exec -e PGPASSWORD='VOTRE_MOT_DE_PASSE' -it postgres-produits \
psql -U fst_user -d ms_produits_db \
-c "SELECT current_user, current_database();"
```

---

# 17. Ordre de démarrage

L'ordre recommandé est :

```text
1. PostgreSQL
       ↓
2. Config Server : 8888
       ↓
3. Eureka Server : 8761
       ↓
4. ms-produits : 8082
       ↓
5. ms-commandes : 8083
       ↓
6. API Gateway : 8081
       ↓
7. Keycloak : 8080
       ↓
8. Tests
```

> Dans l'état actuel du projet, **API Gateway et Keycloak ne sont pas encore finalisés**. Les étapes 1 à 5 constituent l'environnement actuellement fonctionnel.

---

# 18. Arrêter les applications Spring Boot

Dans chaque terminal où une application Spring Boot est en cours d'exécution :

```text
Ctrl + C
```

Arrêter notamment :

1. `ms-commandes`
2. `ms-produits`
3. Eureka Server
4. Config Server

Lorsque l'API Gateway et Keycloak seront ajoutés, ils devront également être arrêtés.

---

# 19. Arrêter PostgreSQL

Depuis la racine du projet :

```bash
docker compose down
```

Cette commande arrête les conteneurs PostgreSQL.

Les données sont conservées dans les volumes Docker :

```text
postgres-produits-data
postgres-commandes-data
```

Pour supprimer également les données des bases :

```bash
docker compose down -v
```

> ⚠️ Cette dernière commande supprime les volumes Docker et donc les données PostgreSQL enregistrées dans ces volumes.

---

# 20. Résumé des URLs

| Service | URL |
|---|---|
| Keycloak | `http://localhost:8080` |
| API Gateway | `http://localhost:8081` |
| ms-produits | `http://localhost:8082` |
| ms-commandes | `http://localhost:8083` |
| Eureka | `http://localhost:8761` |
| Config Server | `http://localhost:8888` |

---

# 21. Dépannage

### Le port est déjà utilisé

Identifier le processus :

```bash
sudo lsof -i :8082
```

Remplacer `8082` par le port concerné.

---

### Le microservice ne trouve pas Config Server

Vérifier que Config Server fonctionne :

```bash
curl http://localhost:8888/ms-produits/default
```

Puis vérifier que le fichier correspondant existe dans :

```text
config-repo/ms-produits.yml
```

---

### Le microservice n'apparaît pas dans Eureka

Vérifier :

```text
http://localhost:8761
```

Puis vérifier que Eureka Server est démarré avant les microservices.

---

### Erreur de connexion PostgreSQL

Vérifier les conteneurs :

```bash
docker ps
```

Puis vérifier que le fichier `.env` contient :

```env
POSTGRES_PASSWORD=VOTRE_MOT_DE_PASSE
```

Avant de lancer `ms-produits` ou `ms-commandes`, charger les variables :

```bash
export $(grep -v '^#' .env | xargs)
```

---

# 22. Nettoyage du projet

Pour arrêter les bases :

```bash
docker compose down
```

Pour arrêter et supprimer également les volumes :

```bash
docker compose down -v
```

Pour reconstruire les microservices après une modification :

```bash
mvn clean package -DskipTests
```

---

## Statut

🚧 **TP en cours de réalisation**

Fonctionnalités actuellement opérationnelles :

- PostgreSQL avec Docker Compose
- Config Server
- Eureka Server
- `ms-produits`
- `ms-commandes`
- Communication OpenFeign
- Découverte de services avec Eureka
- Création et consultation des produits
- Création et consultation des commandes
- Modification du statut des commandes

Prochaines étapes :

- API Gateway
- Keycloak
- Authentification
- Autorisation par rôles
- Tests Postman complets
- Finalisation de la documentation