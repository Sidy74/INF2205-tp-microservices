package com.fst.ms_commandes.repository;

import com.fst.ms_commandes.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    Optional<Commande> findByNumero(String numero);
    @Query("SELECT DISTINCT c FROM Commande c LEFT JOIN FETCH c.lignes")
    List<Commande> findAllAvecLignes();

    @Query("SELECT DISTINCT c FROM Commande c LEFT JOIN FETCH c.lignes WHERE c.id = :id")
    Optional<Commande> findByIdAvecLignes(@Param("id") Long id);

    @Query("""
            SELECT DISTINCT c
            FROM Commande c
            LEFT JOIN FETCH c.lignes
            WHERE c.clientId = :clientId
            ORDER BY c.dateCommande DESC
            """)
    List<Commande> findAllParClientAvecLignes(@Param("clientId") String clientId);
}
