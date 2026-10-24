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

## Endpoints principaux

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

## Port

Le microservice écoute sur le port :

```text
8082
```

URL locale :

```text
http://localhost:8082
```

## Gestion des erreurs

Le microservice utilise un gestionnaire global des exceptions pour notamment gérer :

- produit introuvable : `404 NOT FOUND` ;
- référence de produit déjà existante : `409 CONFLICT`.

## État

🚧 **En cours de développement**

La configuration PostgreSQL centralisée, Eureka, le Config Server et l'API Gateway seront intégrés conformément aux étapes du TP.