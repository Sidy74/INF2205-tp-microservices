# ms-commandes

Microservice de gestion des commandes dans l'architecture microservices du TP **INF2205 — Technologies du Web**.

## Rôle

`ms-commandes` gère :

- la création des commandes ;
- les lignes de commande ;
- la consultation d'une commande ;
- la liste des commandes ;
- le changement de statut d'une commande ;
- la vérification de l'existence et de la disponibilité des produits via `ms-produits`.

## Fonctionnalités

- Création d'une commande avec au moins une ligne.
- Vérification de l'existence du produit.
- Vérification que le produit est actif.
- Vérification de la disponibilité du stock.
- Récupération du prix du produit au moment de la commande.
- Calcul automatique du montant total.
- Consultation d'une commande par son identifiant.
- Liste des commandes.
- Gestion du statut d'une commande.
- Une commande livrée ne peut pas être directement annulée.
- Gestion centralisée des erreurs.

## Communication interservices

La communication avec `ms-produits` est réalisée avec **OpenFeign**.

```text
ms-commandes
     |
     | OpenFeign
     v
ms-produits


## Endpoints

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/commandes` | Créer une commande |
| GET | `/api/commandes` | Lister les commandes |
| GET | `/api/commandes/{id}` | Consulter une commande |
| PATCH | `/api/commandes/{id}/statut` | Modifier le statut |

## Port

```text
8083mandes/{id}/statut` | Modifier le statut |