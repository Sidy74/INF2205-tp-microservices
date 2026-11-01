package com.fst.ms_commandes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class CommandeDTO {

    private Long id;

    private String numero;

    private String clientId;

    @NotEmpty(message = "La commande doit contenir au moins une ligne")
    private List<@Valid LigneCommandeDTO> lignes;

    private BigDecimal montantTotal;

    private String statut;
}
