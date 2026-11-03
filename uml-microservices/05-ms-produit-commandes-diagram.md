classDiagram
    direction LR

    namespace ms_produits {
        class Produit {
            +Long id
            +String reference
            +String nom
            +String description
            +BigDecimal prix
            +Integer quantiteStock
            +Boolean actif
        }
    }

    namespace ms_commandes {
        class Commande {
            +Long id
            +String numero
            +String clientId
            +LocalDateTime dateCommande
            +StatutCommande statut
            +BigDecimal montantTotal
            +calculerMontantTotal()
            +changerStatut()
        }

        class LigneCommande {
            +Long id
            +Long produitId
            +String referenceProduit
            +String nomProduit
            +Integer quantite
            +BigDecimal prixUnitaire
            +calculerSousTotal()
        }

        class StatutCommande {
            <<enumeration>>
            EN_ATTENTE
            VALIDEE
            EXPEDIEE
            LIVREE
            ANNULEE
        }
    }

    Commande "1" *-- "0..*" LigneCommande : contient
    Commande --> StatutCommande : possède
    LigneCommande ..> Produit : informations via OpenFeign
