package com.fst.ms_commandes.exception;

public class CommandeNotFoundException extends RuntimeException {

    public CommandeNotFoundException(Long id) {
        super("Commande introuvable avec l'id : " + id);
    }
}
