package com.fst.ms_produits.controller;

import com.fst.ms_produits.dto.ProduitRequestDto;
import com.fst.ms_produits.dto.ProduitResponseDto;
import com.fst.ms_produits.service.ProduitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {
    private final ProduitService produitService;

    @PostMapping
    public ResponseEntity<ProduitResponseDto> creer(
            @Valid @RequestBody ProduitRequestDto demande
    ){
        ProduitResponseDto produitResponseDto = produitService.creer(demande);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(produitResponseDto);

    }

    @GetMapping("/page")
    public ResponseEntity<Page<ProduitResponseDto>> listerAvecPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ResponseEntity.ok(
                produitService.listerAvecPagination(
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }
    @GetMapping
    public ResponseEntity<List<ProduitResponseDto>> lister() {
        return ResponseEntity.ok(produitService.lister());
    }

    @GetMapping("/recherche")
    public ResponseEntity<List<ProduitResponseDto>> rechercher(
            @RequestParam String nom) {

        return ResponseEntity.ok(produitService.rechercher(nom));
    }
    @GetMapping("/actifs")
    public ResponseEntity<List<ProduitResponseDto>> listerActifs() {
        return ResponseEntity.ok(produitService.listerActifs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduitResponseDto> obtenirParId(@PathVariable Long id) {
        return ResponseEntity.ok(produitService.obtenirParId(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProduitResponseDto> modifier(
            @PathVariable Long id,
            @Valid @RequestBody ProduitRequestDto demande) {

        return ResponseEntity.ok(
                produitService.modifier(id, demande)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        produitService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/disponible")
    public ResponseEntity<Boolean> estDisponible(
            @PathVariable Long id,
            @RequestParam Integer quantite) {

        return ResponseEntity.ok(
                produitService.estDisponible(id, quantite)
        );
    }
}
