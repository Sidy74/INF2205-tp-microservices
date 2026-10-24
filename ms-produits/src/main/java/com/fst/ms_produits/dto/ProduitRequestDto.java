package com.fst.ms_produits.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProduitRequestDto {

    @NotBlank
    private String reference;

    @NotBlank
    private String nom;

    private String description;

    @NotNull
    @PositiveOrZero
    private BigDecimal prix;

    @NotNull
    @PositiveOrZero
    private Integer quantiteStock;

    @NotNull
    private Boolean actif;
}