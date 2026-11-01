package com.fst.ms_commandes.controller;

import com.fst.ms_commandes.config.SecurityConfig;
import com.fst.ms_commandes.dto.CommandeDTO;
import com.fst.ms_commandes.service.CommandeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CommandeController.class)
@Import(SecurityConfig.class)
class CommandeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommandeService commandeService;

    @MockitoBean
    private JwtDecoder jwtDecoder;
    @Test
    void listerCommandesParClient_avecGestionnaire_doitRetourner200() throws Exception {

        CommandeDTO commande = new CommandeDTO();
        commande.setId(1L);
        commande.setClientId("client1");
        commande.setStatut("EN_ATTENTE");

        when(commandeService.listerParClient("client1"))
                .thenReturn(List.of(commande));

        mockMvc.perform(
                        get("/api/commandes/client/client1")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_GESTIONNAIRE")
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].clientId").value("client1"))
                .andExpect(jsonPath("$[0].statut").value("EN_ATTENTE"));
    }

    @Test
    void creerCommande_avecClient_doitRetourner201() throws Exception {

        CommandeDTO resultat = new CommandeDTO();
        resultat.setId(1L);
        resultat.setClientId("client1");
        resultat.setStatut("EN_ATTENTE");

        when(commandeService.creer(any(CommandeDTO.class)))
                .thenReturn(resultat);

        mockMvc.perform(
                        post("/api/commandes")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENT")
                                ))
                                .contentType("application/json")
                                .content("""
                                        {
                                          "clientId": "client1",
                                          "lignes": [
                                            {
                                              "produitId": 1,
                                              "quantite": 2
                                            }
                                          ]
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clientId").value("client1"))
                .andExpect(jsonPath("$.statut").value("EN_ATTENTE"));
    }
    @Test
    void listerCommandesParClient_avecClient_doitRetourner403() throws Exception {

        mockMvc.perform(
                        get("/api/commandes/client/client1")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENT")
                                ))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void creerCommande_sansAuthentification_doitRetourner401() throws Exception {

        mockMvc.perform(
                        post("/api/commandes")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "clientId": "client1",
                                          "lignes": [
                                            {
                                              "produitId": 1,
                                              "quantite": 2
                                            }
                                          ]
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized());
    }


    @Test
    void creerCommande_avecRoleInsuffisant_doitRetourner403() throws Exception {

        mockMvc.perform(
                        post("/api/commandes")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_GESTIONNAIRE")
                                ))
                                .contentType("application/json")
                                .content("""
                                        {
                                          "clientId": "client1",
                                          "lignes": [
                                            {
                                              "produitId": 1,
                                              "quantite": 2
                                            }
                                          ]
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void obtenirCommande_avecGestionnaire_doitRetourner200() throws Exception {

        CommandeDTO resultat = new CommandeDTO();
        resultat.setId(1L);
        resultat.setClientId("client1");
        resultat.setStatut("EN_ATTENTE");

        when(commandeService.obtenirParId(1L))
                .thenReturn(resultat);

        mockMvc.perform(
                        get("/api/commandes/1")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_GESTIONNAIRE")
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clientId").value("client1"));
    }


    @Test
    void obtenirCommande_avecClient_doitRetourner403() throws Exception {

        mockMvc.perform(
                        get("/api/commandes/1")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENT")
                                ))
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void obtenirCommande_sansAuthentification_doitRetourner401() throws Exception {

        mockMvc.perform(
                        get("/api/commandes/1")
                )
                .andExpect(status().isUnauthorized());
    }


    @Test
    void creerCommande_avecQuantiteNegative_doitRetourner400() throws Exception {

        mockMvc.perform(
                        post("/api/commandes")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENT")
                                ))
                                .contentType("application/json")
                                .content("""
                                        {
                                          "clientId": "client1",
                                          "lignes": [
                                            {
                                              "produitId": 1,
                                              "quantite": -1
                                            }
                                          ]
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void changerStatut_avecGestionnaire_doitRetourner200() throws Exception {

        CommandeDTO resultat = new CommandeDTO();
        resultat.setId(1L);
        resultat.setClientId("client1");
        resultat.setStatut("LIVREE");

        when(commandeService.changerStatut(
                1L,
                com.fst.ms_commandes.entity.StatutCommande.LIVREE
        )).thenReturn(resultat);

        mockMvc.perform(
                        patch("/api/commandes/1/statut")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_GESTIONNAIRE")
                                ))
                                .param("statut", "LIVREE")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("LIVREE"));
    }


    @Test
    void changerStatut_avecClient_doitRetourner403() throws Exception {

        mockMvc.perform(
                        patch("/api/commandes/1/statut")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_CLIENT")
                                ))
                                .param("statut", "LIVREE")
                )
                .andExpect(status().isForbidden());
    }
}