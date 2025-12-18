package com.eadl.suivi_academique.services.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eadl.suivi_academique.dto.SalleDTO;
import com.eadl.suivi_academique.entities.Salle;
import com.eadl.suivi_academique.mappers.SalleMapper;
import com.eadl.suivi_academique.repositories.SalleRepository;
import com.eadl.suivi_academique.services.exceptions.salleexception.InvalidSalleException;
import com.eadl.suivi_academique.services.interfaces.SalleInterface;
import com.eadl.suivi_academique.utils.SalleStatus;

@Service
public class SalleService implements SalleInterface {

    @Autowired
    private SalleRepository salleRepository;

    @Autowired
    private SalleMapper salleMapper;

    // CREATE - Créer une nouvelle salle
    public SalleDTO createSalle(SalleDTO salleDTO) {

        //Validation des données
        if (salleDTO.getStatusSalle() == null && salleDTO.getCodeSalle() == null) {

            throw new InvalidSalleException("Le code de la salle et le statut sont obligatoires");
            
        }
        Salle salle = salleMapper.toEntity(salleDTO);
        Salle savedSalle = salleRepository.save(salle);
        return salleMapper.toDTO(savedSalle);
    }

    // READ - Récupérer toutes les salles
    public List<SalleDTO> getAllSalles() {
        List<Salle> salles = salleRepository.findAll();
        return salles.stream()
                .map(salleMapper::toDTO)
                .toList();
    }

    // READ - Récupérer une salle par son code
    public SalleDTO getSalleByCode(String codeSalle) {
        Optional<Salle> salle = salleRepository.findById(codeSalle);
        return salle.map(salleMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Salle non trouvée avec le code: " + codeSalle));
    }

    // READ - Récupérer les salles avec une contenance minimum
    public List<SalleDTO> getSallesByMinContenance(int minContenance) {
        List<Salle> salles = salleRepository.findByContenanceGreaterThanEquals(minContenance);
        return salles.stream()
                .map(salleMapper::toDTO)
                .toList();
    }

    // READ - Récupérer les salles par statut
    public List<SalleDTO> getSallesByStatus(SalleStatus status) {
        
        List<Salle> allSalles = salleRepository.findAll();
        return allSalles.stream()
                .filter(salle -> salle.getStatusSalle() == status)
                .map(salleMapper::toDTO)
                .toList();
    }

    // UPDATE - Mettre à jour une salle
    public SalleDTO updateSalle(String codeSalle, SalleDTO salleDTO) {
        Salle salle = salleRepository.findById(codeSalle)
                .orElseThrow(() -> new RuntimeException("Salle non trouvée avec le code: " + codeSalle));

        // Mettre à jour les champs
        salle.setDescSalle(salleDTO.getDescSalle());
        salle.setContenance(salleDTO.getContenance());
        salle.setStatusSalle(SalleStatus.valueOf(salleDTO.getStatusSalle()));

        Salle updatedSalle = salleRepository.save(salle);
        return salleMapper.toDTO(updatedSalle);
    }

    // DELETE - Supprimer une salle
    public void deleteSalle(String codeSalle) {
        Salle salle = salleRepository.findById(codeSalle)
                .orElseThrow(() -> new RuntimeException("Salle non trouvée avec le code: " + codeSalle));
        salleRepository.delete(salle);
    }

    
    // Vérifier si une salle existe
    public boolean salleExists(String codeSalle) {
        return salleRepository.existsById(codeSalle);
    }

    // Compter le nombre total de salles
    public long countSalles() {
        return salleRepository.count();
    }

}
