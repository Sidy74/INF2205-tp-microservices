package com.fst.ms_produits.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProduitResponseDto {

    private Long id;
    private String reference;
    private String nom;
    private String description;
    private BigDecimal prix;
    private Integer quantiteStock;
    private Boolean actif;
}