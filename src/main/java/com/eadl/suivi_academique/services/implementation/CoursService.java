package com.eadl.suivi_academique.services.implementation;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eadl.suivi_academique.dto.CoursDTO;
import com.eadl.suivi_academique.entities.Cours;
import com.eadl.suivi_academique.mappers.CoursMapper;
import com.eadl.suivi_academique.repositories.CoursRepository;
import com.eadl.suivi_academique.services.exceptions.coursexception.CoursNotFoundException;
import com.eadl.suivi_academique.services.exceptions.coursexception.InvalidCoursException;
import com.eadl.suivi_academique.services.interfaces.CoursInterface;

@Service
public class CoursService implements CoursInterface{

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private CoursMapper coursMapper;

    // CREATE - Créer un nouveau cours
    public CoursDTO createCours(CoursDTO coursDTO) {

        //Validation des données
        if(coursDTO.getLabelCours() == null || coursDTO.getLabelCours().isEmpty()){
            throw new InvalidCoursException("Le titre du cours est obligatoire");
        }

        //Conversion DTO vers entité
        Cours cours = coursMapper.toEntity(coursDTO);

        //Sauvegarde en base de données
        Cours savedCours = coursRepository.save(cours);

        //Conversion entité vers DTO
        return coursMapper.toDTO(savedCours);
    }

    // READ - Récupérer tous les cours
    public List<CoursDTO> getAllCours() {
        List<Cours> coursList = coursRepository.findAll();
        
        // return coursMapper.toDTOs(coursList);
        return coursList.stream().map(coursMapper::toDTO).toList();
    }

    // READ - Récupérer un cours par son code
    public CoursDTO getCoursByCode(String codeCours) {
        //Verification de l'existence du cours
        Cours cours = coursRepository.findById(codeCours)
                .orElseThrow(() -> new CoursNotFoundException("Cours non trouvé avec le code: " + codeCours));

        //Conversion entité vers DTO
        return coursMapper.toDTO(cours);
    }

    // READ - Récupérer les cours par label (recherche)
    public List<CoursDTO> getCoursByLabel(String labelCours) {
        List<Cours> coursList = coursRepository.findByLabelCoursContainingIgnoreCase(labelCours);
        return coursList.stream()
                .map(coursMapper::toDTO)
                .toList();
    }

    // UPDATE - Mettre à jour un cours
    public CoursDTO updateCours(String codeCours, CoursDTO coursDTO) {
        Cours cours = coursRepository.findById(codeCours)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé avec le code: " + codeCours));

        // Mettre à jour les champs
        cours.setLabelCours(coursDTO.getLabelCours());
        cours.setDescCours(coursDTO.getDescCours());
        cours.setNbCreditCours(coursDTO.getNbCreditCours());
        cours.setNbHeureCours(coursDTO.getNbHeureCours());

        Cours updatedCours = coursRepository.save(cours);
        return coursMapper.toDTO(updatedCours);
    }

    // DELETE - Supprimer un cours
    public void deleteCours(String codeCours) {
        Cours cours = coursRepository.findById(codeCours)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé avec le code: " + codeCours));
        coursRepository.delete(cours);
    }

    // Vérifier si un cours existe
    public boolean coursExists(String codeCours) {
        return coursRepository.existsById(codeCours);
    }

    // Compter le nombre total de cours
    public long countCours() {
        return coursRepository.count();
    }

    // Récupérer les cours avec un nombre de crédits supérieur à
    public List<CoursDTO> getCoursByMinCredit(int minCredit) {
        List<Cours> coursList = coursRepository.findByNbCreditCoursGreaterThanEqual(minCredit);
        return coursList.stream()
                .map(coursMapper::toDTO)
                .toList();
    }

    // Récupérer les cours avec un nombre d'heures supérieur à
    public List<CoursDTO> getCoursByMinHeures(int minHeures) {
        List<Cours> coursList = coursRepository.findByNbHeureCoursGreaterThanEqual(minHeures);
        return coursList.stream()
                .map(coursMapper::toDTO)
                .toList();
    }
}
