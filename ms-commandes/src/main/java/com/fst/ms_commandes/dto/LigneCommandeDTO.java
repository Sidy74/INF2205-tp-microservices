package com.fst.ms_commandes.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LigneCommandeDTO {
    @NotNull
    private Long produitId;

    @NotNull
    @Positive
    private Integer quantite;
}
