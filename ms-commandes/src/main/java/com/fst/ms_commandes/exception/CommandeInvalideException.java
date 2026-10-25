package com.fst.ms_commandes.exception;

public class CommandeInvalideException  extends RuntimeException {

    public CommandeInvalideException(String message) {
        super(message);
    }
}