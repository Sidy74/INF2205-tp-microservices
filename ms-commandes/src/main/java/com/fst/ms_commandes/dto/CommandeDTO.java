package com.fst.ms_commandes.dto;

import jakarta.validation.Valid;
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

    @Valid
    private List<LigneCommandeDTO> lignes;

    private BigDecimal montantTotal;

    private String statut;
}
