package com.fst.ms_produits.service;

import com.fst.ms_produits.dto.ProduitRequestDto;
import com.fst.ms_produits.dto.ProduitResponseDto;
import com.fst.ms_produits.entity.Produit;
import com.fst.ms_produits.exception.ProduitNotFoundException;
import com.fst.ms_produits.exception.ProduitReferenceAlreadyExistsException;
import com.fst.ms_produits.repository.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ProduitService produitService;

    @Test
    void creerProduit_avecReferenceDisponible_doitCreerLeProduit() {
        ProduitRequestDto demande = new ProduitRequestDto();
        demande.setReference("P001");
        demande.setNom("Ordinateur");
        demande.setDescription("Ordinateur portable");
        demande.setPrix(new BigDecimal("500000"));
        demande.setQuantiteStock(10);
        demande.setActif(true);

        when(produitRepository.findByReference("P001"))
                .thenReturn(Optional.empty());

        Produit produitSauvegarde = new Produit();
        produitSauvegarde.setId(1L);
        produitSauvegarde.setReference("P001");
        produitSauvegarde.setNom("Ordinateur");
        produitSauvegarde.setDescription("Ordinateur portable");
        produitSauvegarde.setPrix(new BigDecimal("500000"));
        produitSauvegarde.setQuantiteStock(10);
        produitSauvegarde.setActif(true);

        when(produitRepository.save(any(Produit.class)))
                .thenReturn(produitSauvegarde);

        ProduitResponseDto resultat = produitService.creer(demande);

        assertNotNull(resultat);
        assertEquals(1L, resultat.getId());
        assertEquals("P001", resultat.getReference());
        assertEquals("Ordinateur", resultat.getNom());
        assertEquals(new BigDecimal("500000"), resultat.getPrix());
        assertEquals(10, resultat.getQuantiteStock());
        assertTrue(resultat.getActif());

        verify(produitRepository).findByReference("P001");
        verify(produitRepository).save(any(Produit.class));
    }

    @Test
    void creerProduit_avecReferenceExistante_doitLeverUneException() {
        ProduitRequestDto demande = new ProduitRequestDto();
        demande.setReference("P001");
        demande.setNom("Ordinateur");
        demande.setPrix(new BigDecimal("500000"));
        demande.setQuantiteStock(10);
        demande.setActif(true);

        Produit produitExistant = new Produit();
        produitExistant.setId(1L);
        produitExistant.setReference("P001");

        when(produitRepository.findByReference("P001"))
                .thenReturn(Optional.of(produitExistant));

        assertThrows(
                ProduitReferenceAlreadyExistsException.class,
                () -> produitService.creer(demande)
        );

        verify(produitRepository).findByReference("P001");
        verify(produitRepository, never()).save(any(Produit.class));
    }

    @Test
    void obtenirParId_produitExistant_doitRetournerLeProduit() {
        Produit produit = new Produit();
        produit.setId(1L);
        produit.setReference("P001");
        produit.setNom("Ordinateur");
        produit.setPrix(new BigDecimal("500000"));
        produit.setQuantiteStock(10);
        produit.setActif(true);

        when(produitRepository.findById(1L))
                .thenReturn(Optional.of(produit));

        ProduitResponseDto resultat = produitService.obtenirParId(1L);

        assertNotNull(resultat);
        assertEquals(1L, resultat.getId());
        assertEquals("P001", resultat.getReference());
        assertEquals("Ordinateur", resultat.getNom());

        verify(produitRepository).findById(1L);
    }

    @Test
    void obtenirParId_produitInexistant_doitLeverUneException() {
        when(produitRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProduitNotFoundException.class,
                () -> produitService.obtenirParId(99L)
        );

        verify(produitRepository).findById(99L);
    }

    @Test
    void estDisponible_stockSuffisant_etProduitActif_doitRetournerVrai() {
        Produit produit = new Produit();
        produit.setId(1L);
        produit.setQuantiteStock(10);
        produit.setActif(true);

        when(produitRepository.findById(1L))
                .thenReturn(Optional.of(produit));

        boolean resultat = produitService.estDisponible(1L, 5);

        assertTrue(resultat);
    }

    @Test
    void estDisponible_stockInsuffisant_doitRetournerFaux() {
        Produit produit = new Produit();
        produit.setId(1L);
        produit.setQuantiteStock(3);
        produit.setActif(true);

        when(produitRepository.findById(1L))
                .thenReturn(Optional.of(produit));

        boolean resultat = produitService.estDisponible(1L, 5);

        assertFalse(resultat);
    }

    @Test
    void estDisponible_produitInactif_doitRetournerFaux() {
        Produit produit = new Produit();
        produit.setId(1L);
        produit.setQuantiteStock(10);
        produit.setActif(false);

        when(produitRepository.findById(1L))
                .thenReturn(Optional.of(produit));

        boolean resultat = produitService.estDisponible(1L, 5);

        assertFalse(resultat);
    }

    @Test
    void supprimer_produitExistant_doitSupprimer() {
        when(produitRepository.existsById(1L))
                .thenReturn(true);

        produitService.supprimer(1L);

        verify(produitRepository).existsById(1L);
        verify(produitRepository).deleteById(1L);
    }

    @Test
    void supprimer_produitInexistant_doitLeverUneException() {
        when(produitRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ProduitNotFoundException.class,
                () -> produitService.supprimer(99L)
        );

        verify(produitRepository).existsById(99L);
        verify(produitRepository, never()).deleteById(99L);
    }
}