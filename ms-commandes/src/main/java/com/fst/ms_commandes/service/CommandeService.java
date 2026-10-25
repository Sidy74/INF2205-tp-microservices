package com.fst.ms_commandes.service;

import com.fst.ms_commandes.client.ProduitClient;
import com.fst.ms_commandes.dto.CommandeDTO;
import com.fst.ms_commandes.dto.LigneCommandeDTO;
import com.fst.ms_commandes.dto.ProduitResponseDTO;
import com.fst.ms_commandes.entity.Commande;
import com.fst.ms_commandes.entity.LigneCommande;
import com.fst.ms_commandes.entity.StatutCommande;
import com.fst.ms_commandes.exception.CommandeInvalideException;
import com.fst.ms_commandes.exception.CommandeNotFoundException;
import com.fst.ms_commandes.repository.CommandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeService {
    private final CommandeRepository commandeRepository;
    private final ProduitClient produitClient;

    public CommandeDTO creer(CommandeDTO demande) {

        if (demande.getLignes() == null || demande.getLignes().isEmpty()) {
            throw new CommandeInvalideException(
                    "Une commande doit contenir au moins une ligne"
            );
        }

        List<LigneCommande> lignes = demande.getLignes()
                .stream()
                .map(ligneDTO -> {

                    ProduitResponseDTO produit =
                            produitClient.obtenirProduit(ligneDTO.getProduitId());
                    if (!produit.getActif()) {
                        throw new CommandeInvalideException(
                                "Le produit " + produit.getReference() + " est inactif"
                        );
                    }

                    if (!produitClient.verifierDisponibilite(
                            ligneDTO.getProduitId(),
                            ligneDTO.getQuantite())) {

                        throw new CommandeInvalideException(
                                "Stock insuffisant pour le produit " + produit.getReference()
                        );
                    }

                    LigneCommande ligne = new LigneCommande();
                    ligne.setProduitId(produit.getId());
                    ligne.setReferenceProduit(produit.getReference());
                    ligne.setNomProduit(produit.getNom());
                    ligne.setQuantite(ligneDTO.getQuantite());
                    ligne.setPrixUnitaire(produit.getPrix());

                    return ligne;
                })
                .toList();

        Commande commande = new Commande();

        commande.setNumero(demande.getNumero());
        commande.setClientId(demande.getClientId());
        commande.setDateCommande(LocalDateTime.now());
        commande.setStatut(StatutCommande.EN_ATTENTE);

        commande.setLignes(lignes);

        lignes.forEach(ligne -> ligne.setCommande(commande));

        commande.setMontantTotal(commande.calculerMontantTotal());

        Commande sauvegardee = commandeRepository.save(commande);

        return convertirEnDTO(sauvegardee);
    }

    private CommandeDTO convertirEnDTO(Commande commande) {
        CommandeDTO dto = new CommandeDTO();

        dto.setId(commande.getId());
        dto.setNumero(commande.getNumero());
        dto.setClientId(commande.getClientId());
        dto.setMontantTotal(commande.getMontantTotal());
        dto.setStatut(commande.getStatut().name());

        List<LigneCommandeDTO> lignes = commande.getLignes()
                .stream()
                .map(ligne -> {
                    LigneCommandeDTO ligneDTO = new LigneCommandeDTO();
                    ligneDTO.setProduitId(ligne.getProduitId());
                    ligneDTO.setQuantite(ligne.getQuantite());
                    return ligneDTO;
                })
                .toList();

        dto.setLignes(lignes);

        return dto;
    }

    public CommandeDTO obtenirParId(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new CommandeNotFoundException(id));

        return convertirEnDTO(commande);
    }

    public List<CommandeDTO> lister() {
        return commandeRepository.findAll()
                .stream()
                .map(this::convertirEnDTO)
                .toList();
    }

    public CommandeDTO changerStatut(Long id, StatutCommande nouveauStatut) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new CommandeNotFoundException(id));

        if (commande.getStatut() == StatutCommande.LIVREE
                && nouveauStatut == StatutCommande.ANNULEE) {
            throw new CommandeInvalideException(
                    "Une commande livrée ne peut pas être annulée"
            );
        }

        commande.changerStatut(nouveauStatut);

        return convertirEnDTO(commandeRepository.save(commande));
    }
}
