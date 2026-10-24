package com.fst.ms_produits.service;

import com.fst.ms_produits.dto.ProduitRequestDto;
import com.fst.ms_produits.dto.ProduitResponseDto;
import com.fst.ms_produits.entity.Produit;
import com.fst.ms_produits.exception.ProduitNotFoundException;
import com.fst.ms_produits.exception.ProduitReferenceAlreadyExistsException;
import com.fst.ms_produits.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProduitService {
    private final ProduitRepository produitRepository;

    public ProduitResponseDto creer(ProduitRequestDto demande) {

        if (produitRepository.findByReference(demande.getReference()).isPresent()) {
            throw new ProduitReferenceAlreadyExistsException(demande.getReference());
        }

        Produit produit = new Produit();

        produit.setReference(demande.getReference());
        produit.setNom(demande.getNom());
        produit.setDescription(demande.getDescription());
        produit.setPrix(demande.getPrix());
        produit.setQuantiteStock(demande.getQuantiteStock());
        produit.setActif(demande.getActif());

        Produit sauvegarde = produitRepository.save(produit);

        return toResponse(sauvegarde);
    }



    private  ProduitResponseDto toResponse(Produit produit) {


        ProduitResponseDto response = new ProduitResponseDto();

        response.setId(produit.getId());
        response.setReference(produit.getReference());
        response.setNom(produit.getNom());
        response.setDescription(produit.getDescription());
        response.setPrix(produit.getPrix());
        response.setQuantiteStock(produit.getQuantiteStock());
        response.setActif(produit.getActif());

        return response;
    }
    public ProduitResponseDto obtenirParId(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ProduitNotFoundException(id));
        return toResponse(produit);
    }

    public List<ProduitResponseDto> rechercher(String nom) {
        return produitRepository.findByNomContainingIgnoreCase(nom)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProduitResponseDto> listerActifs() {
        return produitRepository.findByActifTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProduitResponseDto modifier(Long id, ProduitRequestDto demande) {

        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ProduitNotFoundException(id));
        // Vérifier que la nouvelle référence n'appartient pas à un autre produit
        produitRepository.findByReference(demande.getReference())
                .filter(existant -> !existant.getId().equals(id))
                .ifPresent(existant -> {
                    throw new ProduitReferenceAlreadyExistsException(demande.getReference());
                });

        produit.setReference(demande.getReference());
        produit.setNom(demande.getNom());
        produit.setDescription(demande.getDescription());
        produit.setPrix(demande.getPrix());
        produit.setQuantiteStock(demande.getQuantiteStock());
        produit.setActif(demande.getActif());

        return toResponse(produitRepository.save(produit));
    }

    public void supprimer(Long id) {

        if (!produitRepository.existsById(id)) {
            throw new ProduitNotFoundException(id);
        }
        produitRepository.deleteById(id);
    }
    public boolean estDisponible(Long id, Integer quantite) {

        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ProduitNotFoundException(id));

        return produit.getActif()
                && produit.getQuantiteStock() >= quantite;
    }
    public List<ProduitResponseDto> lister() {
        return produitRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
