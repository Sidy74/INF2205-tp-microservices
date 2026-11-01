package com.fst.ms_produits.controller;

import com.fst.ms_produits.dto.ProduitResponseDto;
import com.fst.ms_produits.service.ProduitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import com.fst.ms_produits.config.SecurityConfig;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProduitController.class)
@Import(SecurityConfig.class)
class ProduitControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProduitService produitService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void obtenirProduit_avecUtilisateurAutorise_doitRetourner200() throws Exception {

        ProduitResponseDto produit = new ProduitResponseDto();
        produit.setId(1L);
        produit.setReference("P001");
        produit.setNom("Ordinateur");
        produit.setPrix(new BigDecimal("500000"));
        produit.setQuantiteStock(10);
        produit.setActif(true);

        when(produitService.obtenirParId(1L))
                .thenReturn(produit);

        mockMvc.perform(
                        get("/api/produits/1")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_GESTIONNAIRE")
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reference").value("P001"))
                .andExpect(jsonPath("$.nom").value("Ordinateur"));
    }
    @Test
    void obtenirProduit_sansAuthentification_doitRetourner401() throws Exception {

        mockMvc.perform(get("/api/produits/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenirProduit_avecRoleInsuffisant_doitRetourner403() throws Exception {

        mockMvc.perform(
                        get("/api/produits/1")
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority("ROLE_VENDEUR")
                                ))
                )
                .andExpect(status().isForbidden());
    }
}