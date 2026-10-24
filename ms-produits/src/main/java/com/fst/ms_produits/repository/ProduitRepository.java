package com.fst.ms_produits.repository;

import com.fst.ms_produits.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface  ProduitRepository extends JpaRepository<Produit,Long> {
    Optional<Produit> findByReference(String reference);
    List<Produit> findByNomContainingIgnoreCase  (String nom);
    List<Produit> findByActifTrue();

}
