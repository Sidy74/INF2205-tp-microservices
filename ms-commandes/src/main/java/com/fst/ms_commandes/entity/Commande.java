package com.fst.ms_commandes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "commandes")
@Getter
@Setter
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable=false)
    private String clientId;

    @Column(nullable = false)
    private LocalDateTime dateCommande;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommande statut;

    @Column(nullable = false)
    private BigDecimal montantTotal;

    @OneToMany(
            mappedBy = "commande",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<LigneCommande> lignes = new ArrayList<>();

    public BigDecimal calculerMontantTotal() {
        return lignes.stream()
                .map(LigneCommande::calculerSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public void changerStatut(StatutCommande nouveauStatut) {
        this.statut = nouveauStatut;
    }

}
