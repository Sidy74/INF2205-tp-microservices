package com.fst.ms_commandes.repository;

import com.fst.ms_commandes.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
}
