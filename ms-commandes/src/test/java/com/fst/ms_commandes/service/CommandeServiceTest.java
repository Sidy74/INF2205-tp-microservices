package com.fst.ms_commandes.service;

import com.fst.ms_commandes.dto.CommandeCreationDTO;
import com.fst.ms_commandes.dto.CommandeDTO;
import com.fst.ms_commandes.dto.LigneCommandeDTO;
import com.fst.ms_commandes.dto.ProduitResponseDTO;
import com.fst.ms_commandes.entity.Commande;
import com.fst.ms_commandes.entity.StatutCommande;
import com.fst.ms_commandes.exception.CommandeInvalideException;
import com.fst.ms_commandes.exception.CommandeNotFoundException;
import com.fst.ms_commandes.repository.CommandeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeServiceTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private ProduitServiceClient produitServiceClient;

    @InjectMocks
    private CommandeService commandeService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authentifierClient(String clientId) {

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", clientId)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        JwtAuthenticationToken authentication =
                new JwtAuthenticationToken(jwt);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    @Test
    void creerCommande_sansLigne_doitLeverUneException() {

        authentifierClient("client1");

        CommandeCreationDTO demande = new CommandeCreationDTO();
        demande.setLignes(List.of());

        assertThrows(
                CommandeInvalideException.class,
                () -> commandeService.creer(demande)
        );

        verifyNoInteractions(produitServiceClient);
        verify(commandeRepository, never()).save(any());
    }

    @Test
    void creerCommande_produitInactif_doitLeverUneException() {

        authentifierClient("client1");

        ProduitResponseDTO produit = new ProduitResponseDTO();
        produit.setId(1L);
        produit.setReference("P001");
        produit.setNom("Ordinateur");
        produit.setPrix(new BigDecimal("500000"));
        produit.setActif(false);

        when(produitServiceClient.obtenirProduit(1L))
                .thenReturn(produit);

        LigneCommandeDTO ligne = new LigneCommandeDTO();
        ligne.setProduitId(1L);
        ligne.setQuantite(2);

        CommandeCreationDTO demande = new CommandeCreationDTO();
        demande.setLignes(List.of(ligne));

        assertThrows(
                CommandeInvalideException.class,
                () -> commandeService.creer(demande)
        );

        verify(produitServiceClient).obtenirProduit(1L);

        verify(produitServiceClient, never())
                .verifierDisponibilite(any(), any());

        verify(commandeRepository, never()).save(any());
    }

    @Test
    void creerCommande_stockInsuffisant_doitLeverUneException() {

        authentifierClient("client1");

        ProduitResponseDTO produit = new ProduitResponseDTO();
        produit.setId(1L);
        produit.setReference("P001");
        produit.setNom("Ordinateur");
        produit.setPrix(new BigDecimal("500000"));
        produit.setActif(true);

        when(produitServiceClient.obtenirProduit(1L))
                .thenReturn(produit);

        when(produitServiceClient.verifierDisponibilite(1L, 10))
                .thenReturn(false);

        LigneCommandeDTO ligne = new LigneCommandeDTO();
        ligne.setProduitId(1L);
        ligne.setQuantite(10);

        CommandeCreationDTO demande = new CommandeCreationDTO();
        demande.setLignes(List.of(ligne));

        assertThrows(
                CommandeInvalideException.class,
                () -> commandeService.creer(demande)
        );

        verify(produitServiceClient).obtenirProduit(1L);

        verify(produitServiceClient)
                .verifierDisponibilite(1L, 10);

        verify(commandeRepository, never()).save(any());
    }

    @Test
    void creerCommande_valide_doitCreerLaCommande() {

        authentifierClient("client1");

        ProduitResponseDTO produit = new ProduitResponseDTO();
        produit.setId(1L);
        produit.setReference("P001");
        produit.setNom("Ordinateur");
        produit.setPrix(new BigDecimal("500000"));
        produit.setActif(true);

        when(produitServiceClient.obtenirProduit(1L))
                .thenReturn(produit);

        when(produitServiceClient.verifierDisponibilite(1L, 2))
                .thenReturn(true);

        when(commandeRepository.findByNumero(any()))
                .thenReturn(Optional.empty());

        when(commandeRepository.save(any(Commande.class)))
                .thenAnswer(invocation -> {
                    Commande commande = invocation.getArgument(0);
                    commande.setId(1L);
                    return commande;
                });

        LigneCommandeDTO ligne = new LigneCommandeDTO();
        ligne.setProduitId(1L);
        ligne.setQuantite(2);

        CommandeCreationDTO demande = new CommandeCreationDTO();
        demande.setLignes(List.of(ligne));

        CommandeDTO resultat =
                commandeService.creer(demande);

        assertNotNull(resultat);
        assertEquals(1L, resultat.getId());

        // Le clientId doit venir du JWT
        assertEquals("client1", resultat.getClientId());

        assertEquals(
                StatutCommande.EN_ATTENTE.name(),
                resultat.getStatut()
        );

        assertEquals(
                new BigDecimal("1000000"),
                resultat.getMontantTotal()
        );

        assertEquals(
                1,
                resultat.getLignes().size()
        );

        verify(produitServiceClient).obtenirProduit(1L);

        verify(produitServiceClient)
                .verifierDisponibilite(1L, 2);

        verify(commandeRepository)
                .save(any(Commande.class));
    }

    @Test
    void obtenirParId_commandeExistante_doitRetournerLaCommande() {

        Commande commande = creerCommandeTest(
                1L,
                "CMD-001",
                "client1",
                StatutCommande.EN_ATTENTE
        );

        when(commandeRepository.findByIdAvecLignes(1L))
                .thenReturn(Optional.of(commande));

        CommandeDTO resultat =
                commandeService.obtenirParId(1L);

        assertNotNull(resultat);

        assertEquals(1L, resultat.getId());

        assertEquals(
                "CMD-001",
                resultat.getNumero()
        );

        assertEquals(
                "client1",
                resultat.getClientId()
        );

        assertEquals(
                StatutCommande.EN_ATTENTE.name(),
                resultat.getStatut()
        );
    }

    @Test
    void obtenirParId_commandeInexistante_doitLeverUneException() {

        when(commandeRepository.findByIdAvecLignes(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                CommandeNotFoundException.class,
                () -> commandeService.obtenirParId(99L)
        );
    }

    @Test
    void lister_doitRetournerToutesLesCommandes() {

        Commande commande1 = creerCommandeTest(
                1L,
                "CMD-001",
                "client1",
                StatutCommande.EN_ATTENTE
        );

        Commande commande2 = creerCommandeTest(
                2L,
                "CMD-002",
                "client2",
                StatutCommande.LIVREE
        );

        when(commandeRepository.findAllAvecLignes())
                .thenReturn(List.of(
                        commande1,
                        commande2
                ));

        List<CommandeDTO> resultat =
                commandeService.lister();

        assertEquals(
                2,
                resultat.size()
        );

        assertEquals(
                "CMD-001",
                resultat.get(0).getNumero()
        );

        assertEquals(
                "CMD-002",
                resultat.get(1).getNumero()
        );
    }

    @Test
    void changerStatut_commandeExistante_doitModifierLeStatut() {

        Commande commande = creerCommandeTest(
                1L,
                "CMD-001",
                "client1",
                StatutCommande.EN_ATTENTE
        );

        when(commandeRepository.findById(1L))
                .thenReturn(Optional.of(commande));

        when(commandeRepository.save(any(Commande.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        CommandeDTO resultat =
                commandeService.changerStatut(
                        1L,
                        StatutCommande.LIVREE
                );

        assertEquals(
                StatutCommande.LIVREE.name(),
                resultat.getStatut()
        );

        verify(commandeRepository)
                .save(commande);
    }

    @Test
    void changerStatut_commandeInexistante_doitLeverUneException() {

        when(commandeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                CommandeNotFoundException.class,
                () -> commandeService.changerStatut(
                        99L,
                        StatutCommande.LIVREE
                )
        );

        verify(
                commandeRepository,
                never()
        ).save(any());
    }

    @Test
    void changerStatut_commandeLivreeVersAnnulee_doitLeverUneException() {

        Commande commande = creerCommandeTest(
                1L,
                "CMD-001",
                "client1",
                StatutCommande.LIVREE
        );

        when(commandeRepository.findById(1L))
                .thenReturn(Optional.of(commande));

        assertThrows(
                CommandeInvalideException.class,
                () -> commandeService.changerStatut(
                        1L,
                        StatutCommande.ANNULEE
                )
        );

        verify(
                commandeRepository,
                never()
        ).save(any());
    }

    @Test
    void listerParClient_doitRetournerLesCommandesDuClient() {

        Commande commande1 = creerCommandeTest(
                1L,
                "CMD-001",
                "client1",
                StatutCommande.EN_ATTENTE
        );

        Commande commande2 = creerCommandeTest(
                2L,
                "CMD-002",
                "client1",
                StatutCommande.LIVREE
        );

        when(
                commandeRepository
                        .findAllParClientAvecLignes("client1")
        ).thenReturn(List.of(
                commande1,
                commande2
        ));

        List<CommandeDTO> resultat =
                commandeService.listerParClient("client1");

        assertEquals(
                2,
                resultat.size()
        );

        assertEquals(
                "CMD-001",
                resultat.get(0).getNumero()
        );

        assertEquals(
                "CMD-002",
                resultat.get(1).getNumero()
        );

        assertEquals(
                "client1",
                resultat.get(0).getClientId()
        );

        assertEquals(
                "client1",
                resultat.get(1).getClientId()
        );

        verify(commandeRepository)
                .findAllParClientAvecLignes("client1");
    }

    private Commande creerCommandeTest(
            Long id,
            String numero,
            String clientId,
            StatutCommande statut
    ) {

        Commande commande = new Commande();

        commande.setId(id);
        commande.setNumero(numero);
        commande.setClientId(clientId);
        commande.setStatut(statut);
        commande.setLignes(List.of());

        return commande;
    }
}