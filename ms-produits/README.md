# ms-produits

Microservice de gestion du catalogue des produits dans le cadre du TP **INF2205 — Technologies du Web**.

## Rôle

`ms-produits` est responsable de la gestion du catalogue des produits :

- création d'un produit ;
- consultation d'un produit par son identifiant ;
- liste des produits ;
- liste des produits actifs ;
- recherche par nom ;
- modification d'un produit ;
- suppression d'un produit ;
- vérification de la disponibilité d'un produit.

## Technologies

- Java 21
- Spring Boot 4.1.2
- Spring Web
- Spring Data JPA
- PostgreSQL
- Spring Cloud
- Eureka Discovery Client
- Spring Cloud Config Client
- Spring Boot Actuator
- Jakarta Validation
- Lombok
- Maven

## Communication et infrastructure

`ms-produits` utilise :

- **Spring Cloud Config Client** pour récupérer sa configuration centralisée ;
- **Eureka Discovery Client** pour s'enregistrer auprès d'Eureka ;
- **PostgreSQL** pour la persistance des produits.

```text
Config Server : 8888
       |
       v
ms-produits : 8082
       |
       v
PostgreSQL : 5433

ms-produits
     |
     v
Eureka Server : 8761
```

## Modèle Produit

Un produit contient notamment :

| Champ | Description |
|---|---|
| `id` | Identifiant du produit |
| `reference` | Référence unique du produit |
| `nom` | Nom du produit |
| `description` | Description du produit |
| `prix` | Prix du produit |
| `quantiteStock` | Quantité disponible en stock |
| `actif` | Indique si le produit est actif |

## Endpoints

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/produits` | Créer un produit |
| `GET` | `/api/produits` | Lister les produits |
| `GET` | `/api/produits/{id}` | Obtenir un produit |
| `GET` | `/api/produits/actifs` | Lister les produits actifs |
| `GET` | `/api/produits/recherche?nom=...` | Rechercher par nom |
| `GET` | `/api/produits/{id}/disponible?quantite=...` | Vérifier la disponibilité |
| `PUT` | `/api/produits/{id}` | Modifier un produit |
| `DELETE` | `/api/produits/{id}` | Supprimer un produit |

## Exemples

### Créer un produit

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

### Lister les produits

```bash
curl http://localhost:8082/api/produits
```

### Vérifier la disponibilité

```bash
curl "http://localhost:8082/api/produits/1/disponible?quantite=2"
```

> Remplacer `1` par l'identifiant réel du produit.

## Port

Le microservice écoute sur :

```text
8082
```

URL locale :

```text
http://localhost:8082
```

## Base de données

La base PostgreSQL utilisée par `ms-produits` est :

```text
Base : ms_produits_db
Utilisateur : fst_user
Port hôte : 5433
```

La base est lancée avec Docker Compose depuis la racine du projet.

## Gestion des erreurs

Le microservice utilise un gestionnaire global des exceptions pour notamment gérer :

- produit introuvable : `404 NOT FOUND` ;
- référence de produit déjà existante : `409 CONFLICT` ;
- données de produit invalides : `400 BAD REQUEST`.

## État

✅ **Fonctionnel**

Les fonctionnalités principales du microservice sont opérationnelles et le service est enregistré auprès d'Eureka.