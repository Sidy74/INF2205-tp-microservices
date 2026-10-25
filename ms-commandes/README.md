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
- Spring Boot Actuator
- Jakarta Validation
- Lombok
- Maven

## Communication interservices

La communication avec `ms-produits` est réalisée avec **OpenFeign**.

```text
             Eureka Server
                  |
                  | découverte
                  v
ms-commandes -- OpenFeign --> ms-produits
      |                           |
      v                           v
PostgreSQL                  PostgreSQL
  :5434                        :5433
```

`ms-commandes` ne possède pas de connexion directe à la base de données de `ms-produits`.

Les informations nécessaires sur les produits sont récupérées via l'API de `ms-produits`.

## Exemple de communication

Lorsqu'une commande est créée, `ms-commandes` :

1. récupère le produit demandé ;
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

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/commandes` | Créer une commande |
| `GET` | `/api/commandes` | Lister les commandes |
| `GET` | `/api/commandes/{id}` | Consulter une commande |
| `PATCH` | `/api/commandes/{id}/statut?statut=...` | Modifier le statut |

## Exemples

### Créer une commande

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

### Lister les commandes

```bash
curl http://localhost:8083/api/commandes
```

### Consulter une commande

```bash
curl http://localhost:8083/api/commandes/1
```

> Remplacer `1` par l'identifiant réel de la commande.

### Modifier le statut

```bash
curl -X PATCH "http://localhost:8083/api/commandes/1/statut?statut=VALIDEE"
```

Puis vérifier :

```bash
curl http://localhost:8083/api/commandes/1
```

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
- **OpenFeign** pour communiquer avec `ms-produits`.

Configuration locale minimale :

```properties
spring.application.name=ms-commandes
server.port=8083
spring.config.import=optional:configserver:http://localhost:8888
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
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

La communication avec `ms-produits` via **OpenFeign + Eureka** a été validée, ainsi que la création, la consultation et la modification du statut des commandes.