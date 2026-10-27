package com.fst.ms_commandes.client;

import com.fst.ms_commandes.config.FeignConfig;
import com.fst.ms_commandes.dto.ProduitResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "ms-produits",
        configuration = FeignConfig.class)
public interface ProduitClient {

    @GetMapping("/api/produits/{id}")
    ProduitResponseDTO obtenirProduit(@PathVariable Long id);

    @GetMapping("/api/produits/{id}/disponible")
    boolean verifierDisponibilite(
            @PathVariable Long id,
            @RequestParam Integer quantite
    );

}
