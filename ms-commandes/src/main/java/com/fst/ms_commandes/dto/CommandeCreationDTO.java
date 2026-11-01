package com.fst.ms_commandes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CommandeCreationDTO {

    @NotEmpty(message = "La commande doit contenir au moins une ligne")
    private List<@Valid LigneCommandeDTO> lignes;
}