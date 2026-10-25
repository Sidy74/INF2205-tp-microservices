package com.fst.ms_commandes.repository;

import com.fst.ms_commandes.entity.Commande;
import com.fst.ms_commandes.entity.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {
}
