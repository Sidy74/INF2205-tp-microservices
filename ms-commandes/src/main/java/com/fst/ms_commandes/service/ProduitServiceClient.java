package com.fst.ms_commandes.service;

import com.fst.ms_commandes.client.ProduitClient;
import com.fst.ms_commandes.dto.ProduitResponseDTO;
import com.fst.ms_commandes.exception.ServiceProduitIndisponibleException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProduitServiceClient {

    private final ProduitClient produitClient;

    @CircuitBreaker(
            name = "produitsService",
            fallbackMethod = "obtenirProduitFallback"
    )
    public ProduitResponseDTO obtenirProduit(Long id) {
        return produitClient.obtenirProduit(id);
    }

    public ProduitResponseDTO obtenirProduitFallback(
            Long id,
            Throwable exception
    ) {
        // Préserver le comportement existant pour un produit inexistant.
        if (exception instanceof FeignException.NotFound notFound) {
            throw notFound;
        }

        throw new ServiceProduitIndisponibleException(
                "Le service des produits est momentanément indisponible",
                exception
        );
    }

    @CircuitBreaker(
            name = "produitsService",
            fallbackMethod = "verifierDisponibiliteFallback"
    )
    public boolean verifierDisponibilite(Long id, Integer quantite) {
        return produitClient.verifierDisponibilite(id, quantite);
    }

    public boolean verifierDisponibiliteFallback(
            Long id,
            Integer quantite,
            Throwable exception
    ) {
        if (exception instanceof FeignException.NotFound notFound) {
            throw notFound;
        }

        throw new ServiceProduitIndisponibleException(
                "Impossible de vérifier le stock : le service des produits est momentanément indisponible",
                exception
        );
    }
}