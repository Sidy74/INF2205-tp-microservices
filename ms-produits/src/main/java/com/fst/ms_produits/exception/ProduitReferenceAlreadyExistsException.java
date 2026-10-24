package com.fst.ms_produits.exception;

public class ProduitReferenceAlreadyExistsException extends RuntimeException {

    public ProduitReferenceAlreadyExistsException(String reference) {
        super("Un produit avec la référence " + reference + " existe déjà");
    }
}