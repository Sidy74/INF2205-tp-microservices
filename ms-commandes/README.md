# ms-commandes

Microservice de gestion des commandes dans l'architecture microservices du TP **INF2205 — Technologies du Web**.

## Rôle

`ms-commandes` est responsable de la gestion des commandes :

- création des commandes ;
- gestion des lignes de commande ;
- consultation d'une commande ;
- liste des commandes ;
- changement de statut d'une commande ;
- vérification des produits auprès de `ms-produits`.

## Fonctionnalités

Le microservice permet notamment :

- de créer une commande avec au moins une ligne ;
- de générer automatiquement un numéro de commande ;
- de vérifier l'existence du produit ;
- de vérifier que le produit est actif ;
- de vérifier la disponibilité du stock ;
- de récupérer le prix du produit au moment de la commande ;
- de calculer automatiquement le montant total ;
- de consulter une commande par son identifiant ;
- de lister les commandes ;
- de modifier le statut d'une commande ;
- d'empêcher l'annulation directe d'une commande déjà livrée ;
- de gérer les erreurs de manière centralisée.

## Technologies

- Java 21
- Spring Boot 4.1.2
- Spring Web
- Spring Data JPA
- PostgreSQL
- Spring Cloud
- Eureka Discovery Client
- Spring Cloud Config Client
- Spring Cloud OpenFeign
- Spring Security
- OAuth2 Resource Server
- Keycloak
- Spring Boot Actuator
- Jakarta Validation
- Lombok
- Maven

## Sécurité

L'authentification et l'autorisation sont basées sur **Keycloak** et les tokens **JWT**.

`ms-commandes` fonctionne comme un **OAuth2 Resource Server** et valide les tokens JWT émis par Keycloak.

### Rôles

Les rôles utilisés dans le projet sont :

- `CLIENT`
- `GESTIONNAIRE`
- `ADMIN`

### Autorisations

| Opération | CLIENT | GESTIONNAIRE | ADMIN |
|---|:---:|:---:|:---:|
| Créer une commande | ✅ | ❌ | ❌ |
| Consulter les commandes | ❌ | ✅ | ✅ |
| Modifier le statut | ❌ | ✅ | ✅ |

Les requêtes sans token valide sont refusées avec `401 Unauthorized`.

Les requêtes avec un token valide mais avec un rôle insuffisant sont refusées avec `403 Forbidden`.

## Communication interservices

La communication avec `ms-produits` est réalisée avec **OpenFeign**.

```text
                    Eureka Server
                         |
                         | découverte
                         v
Client → API Gateway → ms-commandes
                           |
                           | OpenFeign + JWT
                           v
                       ms-produits
                         |       |
                         v       v
                    PostgreSQL PostgreSQL
                       :5434       :5433
```

`ms-commandes` ne possède pas de connexion directe à la base de données de `ms-produits`.

Les informations nécessaires sur les produits sont récupérées via l'API de `ms-produits`.

### Transmission du JWT avec OpenFeign

Lorsqu'une requête authentifiée arrive dans `ms-commandes`, le token JWT est récupéré depuis le contexte de sécurité et transmis automatiquement à `ms-produits` par un `RequestInterceptor` Feign.

Cela permet à `ms-produits` de vérifier lui-même le token reçu.

```text
Client
   |
   | JWT
   v
API Gateway
   |
   | JWT
   v
ms-commandes
   |
   | OpenFeign + Authorization: Bearer <JWT>
   v
ms-produits
```

Cette approche permet d'éviter que `ms-commandes` accède directement à la base de données de `ms-produits`.

## Exemple de communication

Lorsqu'une commande est créée, `ms-commandes` :

1. récupère le produit via OpenFeign ;
2. vérifie que le produit est actif ;
3. vérifie la quantité disponible ;
4. récupère son prix ;
5. crée la ligne de commande ;
6. calcule le montant total ;
7. enregistre la commande dans sa propre base.

## Modèle de commande

Une commande contient notamment :

| Élément | Description |
|---|---|
| `id` | Identifiant de la commande |
| `numero` | Numéro unique généré automatiquement |
| `clientId` | Identifiant du client |
| `dateCommande` | Date de création |
| `statut` | État de la commande |
| `montantTotal` | Montant total |
| `lignes` | Liste des lignes de commande |

Une ligne de commande contient notamment :

| Élément | Description |
|---|---|
| `produitId` | Identifiant du produit |
| `referenceProduit` | Référence du produit |
| `nomProduit` | Nom du produit |
| `quantite` | Quantité commandée |
| `prixUnitaire` | Prix du produit au moment de la commande |

## Statuts

Les statuts disponibles sont :

```text
EN_ATTENTE
VALIDEE
EXPEDIEE
LIVREE
ANNULEE
```

Une commande livrée ne peut pas être directement annulée.

## Endpoints

| Méthode | Endpoint | Description | Accès |
|---|---|---|---|
| `POST` | `/api/commandes` | Créer une commande | CLIENT |
| `GET` | `/api/commandes` | Lister les commandes | GESTIONNAIRE / ADMIN |
| `GET` | `/api/commandes/{id}` | Consulter une commande | GESTIONNAIRE / ADMIN |
| `PATCH` | `/api/commandes/{id}/statut?statut=...` | Modifier le statut | GESTIONNAIRE / ADMIN |

## Exemples

Les exemples ci-dessous sont donnés pour un accès direct au microservice sur le port `8083`. Dans l'architecture complète, les requêtes passent normalement par l'API Gateway sur le port `8081`.

### Créer une commande

```bash
curl -i -X POST http://localhost:8083/api/commandes \
  -H "Authorization: Bearer $TOKEN_CLIENT" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": "1",
    "lignes": [
      {
        "produitId": 2,
        "quantite": 1
      }
    ]
  }'
```

### Lister les commandes

```bash
curl -i http://localhost:8083/api/commandes \
  -H "Authorization: Bearer $TOKEN_GESTIONNAIRE"
```

### Consulter une commande

```bash
curl -i http://localhost:8083/api/commandes/1 \
  -H "Authorization: Bearer $TOKEN_GESTIONNAIRE"
```

> Remplacer `1` par l'identifiant réel de la commande.

### Modifier le statut

```bash
curl -i -X PATCH \
  "http://localhost:8083/api/commandes/1/statut?statut=VALIDEE" \
  -H "Authorization: Bearer $TOKEN_GESTIONNAIRE"
```

## Tests de sécurité réalisés

Les principaux scénarios ont été testés via l'API Gateway.

| Scénario | Résultat |
|---|---:|
| CLIENT → création d'une commande | `201 Created` ✅ |
| CLIENT → consultation des commandes | `403 Forbidden` ✅ |
| GESTIONNAIRE → consultation des commandes | `200 OK` ✅ |
| GESTIONNAIRE → modification du statut | `200 OK` ✅ |
| CLIENT → modification du statut | `403 Forbidden` ✅ |
| OpenFeign → accès sécurisé à `ms-produits` avec JWT | Fonctionnel ✅ |

Exemple de commande créée lors des tests :

```json
{
  "clientId": "1",
  "id": 11,
  "lignes": [
    {
      "produitId": 2,
      "quantite": 1
    }
  ],
  "montantTotal": 450000.00,
  "numero": "CMD-DC075127",
  "statut": "EN_ATTENTE"
}
```

Le statut de cette commande a ensuite été modifié avec succès vers `VALIDEE` par un utilisateur ayant le rôle `GESTIONNAIRE`.

## Port

Le microservice écoute sur :

```text
8083
```

URL locale :

```text
http://localhost:8083
```

## Base de données

La base PostgreSQL utilisée par `ms-commandes` est :

```text
Base : ms_commandes_db
Utilisateur : fst_user
Port hôte : 5434
```

La base est lancée avec Docker Compose depuis la racine du projet.

## Configuration

`ms-commandes` utilise :

- **Spring Cloud Config Client** pour récupérer sa configuration ;
- **Eureka Discovery Client** pour découvrir les services ;
- **OpenFeign** pour communiquer avec `ms-produits` ;
- **Spring Security / OAuth2 Resource Server** pour valider les JWT.

Configuration locale minimale :

```properties
spring.application.name=ms-commandes
server.port=8083
spring.config.import=optional:configserver:http://localhost:8888
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/gestion-commerciale
```

## Gestion des erreurs

Le microservice utilise une gestion centralisée des exceptions pour notamment traiter :

- commande introuvable ;
- commande invalide ;
- produit inactif ;
- stock insuffisant ;
- données de commande invalides.

## État

✅ **Fonctionnel**

Les fonctionnalités principales du microservice sont opérationnelles.

La communication avec `ms-produits` via **OpenFeign + Eureka** a été validée, y compris la transmission du JWT.

La sécurité avec **Keycloak / OAuth2 / JWT** a également été validée avec les différents rôles.

Les principaux scénarios de création, consultation, modification du statut et contrôle des autorisations ont été testés avec succès.