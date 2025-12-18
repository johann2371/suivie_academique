package com.eadl.suivi_academique.services.implementation;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eadl.suivi_academique.dto.AffectationDTO;
import com.eadl.suivi_academique.dto.CoursDTO;
import com.eadl.suivi_academique.dto.PersonnelDTO;
import com.eadl.suivi_academique.entities.Affectation;
import com.eadl.suivi_academique.entities.AffectationId;
import com.eadl.suivi_academique.entities.Cours;
import com.eadl.suivi_academique.entities.Personnel;
import com.eadl.suivi_academique.mappers.AffectationMapper;
import com.eadl.suivi_academique.mappers.CoursMapper;
import com.eadl.suivi_academique.mappers.PersonnelMapper;
import com.eadl.suivi_academique.repositories.AffectationRepository;
import com.eadl.suivi_academique.repositories.CoursRepository;
import com.eadl.suivi_academique.repositories.PersonnelRepository;
import com.eadl.suivi_academique.services.interfaces.AffectationInterface;

@Service
public class AffectationService implements AffectationInterface{

    @Autowired
    private AffectationRepository affectationRepository;

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired 
    private CoursRepository coursRepository;

    @Autowired
    private AffectationMapper affectationMapper;

    @Autowired
    private PersonnelMapper personnelMapper;

    @Autowired 
    private CoursMapper coursMapper;

    public AffectationDTO create(AffectationDTO dto){

        //recupérer l'entité associée à codePersonnel et codeCours
        Personnel personnel = personnelRepository.findById(dto.getCodePersonnel())
            .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

        Cours cours = coursRepository.findById(dto.getCodeCours())
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        
        PersonnelDTO personnelDTO = personnelMapper.toDTO(personnel);
        CoursDTO coursDTO = coursMapper.toDTO(cours);


        Affectation aff = new Affectation();

        // Clé composite
        AffectationId id = new AffectationId(null, null);

        id.setCodePersonnel(dto.getCodePersonnel());
        
        id.setCodeCours(dto.getCodeCours());

        aff.setCodeAffectation(id);
        aff.setPersonnel(personnel);
        aff.setCours(cours);

        Affectation saved = affectationRepository.save(aff);

    
        AffectationDTO respDto = new AffectationDTO(saved.getPersonnel().getCodePersonnel(), saved.getCours().getCodeCours(), personnelDTO, coursDTO);

        return respDto;
        

    }

    public List<AffectationDTO> getAll() {
        List<Affectation> affectations = affectationRepository.findAll();

        return affectationMapper.toDtos(affectations);
    }


    public AffectationDTO getById(String codeCours, String codePersonnel) {

        Personnel personnel = personnelRepository.findById(codePersonnel)
            .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

        Cours cours = coursRepository.findById(codeCours)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        PersonnelDTO personnelDTO = personnelMapper.toDTO(personnel);
        CoursDTO coursDTO = coursMapper.toDTO(cours);

        AffectationId id = new AffectationId(codeCours, codePersonnel);

        Affectation affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Affectation non trouvée pour cours=" + codeCours + " et personnel=" + codePersonnel
                ));

        return new AffectationDTO(
                affectation.getPersonnel().getCodePersonnel(),
                affectation.getCours().getCodeCours(),
                personnelDTO,
                coursDTO

        );
    }


    public void delete(String codeCours, String codePersonnel) {

        AffectationId id = new AffectationId(codeCours, codePersonnel);

        if (!affectationRepository.existsById(id)) {
            throw new RuntimeException(
                    "Affectation non trouvée pour cours=" + codeCours + " et personnel=" + codePersonnel
            );
        }

        affectationRepository.deleteById(id);
    }


}
