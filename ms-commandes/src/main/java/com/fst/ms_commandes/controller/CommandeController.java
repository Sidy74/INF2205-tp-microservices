package com.fst.ms_commandes.controller;

import com.fst.ms_commandes.dto.CommandeDTO;
import com.fst.ms_commandes.entity.LigneCommande;
import com.fst.ms_commandes.entity.StatutCommande;
import com.fst.ms_commandes.service.CommandeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CommandeController {
    private final CommandeService  commandeService;

    @PostMapping
    public ResponseEntity<CommandeDTO> creer(
            @Valid @RequestBody CommandeDTO demande) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commandeService.creer(demande));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeDTO> obtenirParId(@PathVariable Long id) {
        return ResponseEntity.ok(commandeService.obtenirParId(id));
    }

    @GetMapping
    public ResponseEntity<List<CommandeDTO>> lister() {
        return ResponseEntity.ok(commandeService.lister());
    }
    @PatchMapping("/{id}/statut")
    public ResponseEntity<CommandeDTO> changerStatut(
            @PathVariable Long id,
            @RequestParam StatutCommande statut) {

        return ResponseEntity.ok(
                commandeService.changerStatut(id, statut)
        );
    }
}
