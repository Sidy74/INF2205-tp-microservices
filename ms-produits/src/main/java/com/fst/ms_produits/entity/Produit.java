package com.fst.ms_produits.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name="produits")
@Getter
@Setter
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String reference;

    @NotBlank
    @Column(nullable=false)
    private String  nom;

    private  String description;

    @NotNull
    @PositiveOrZero
    @Column(nullable=false)
    private BigDecimal prix;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer quantiteStock;

    @NotNull
    @Column(nullable = false)
    private Boolean actif = true;

}
