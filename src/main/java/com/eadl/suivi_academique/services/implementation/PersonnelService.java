package com.eadl.suivi_academique.services.implementation;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.eadl.suivi_academique.dto.PersonnelDTO;
import com.eadl.suivi_academique.entities.Personnel;
import com.eadl.suivi_academique.mappers.PersonnelMapper;
import com.eadl.suivi_academique.repositories.PersonnelRepository;
import com.eadl.suivi_academique.services.interfaces.PersonnelInterface;
import com.eadl.suivi_academique.utils.CodeGenerator;
import com.eadl.suivi_academique.utils.RolePersonnel;

@Service
public class PersonnelService implements PersonnelInterface {

    @Autowired
    private PersonnelRepository personnelRepository;

    @Autowired
    private PersonnelMapper personnelMapper;

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // CREATE - Créer un nouveau personnel
    public PersonnelDTO createPersonnel(PersonnelDTO personnelDTO) {
        // Generate code based on role
        String generatedCode = codeGenerator.generate(personnelDTO.getRolePersonnel());
        if (generatedCode == null) {
            throw new RuntimeException("Impossible de générer un code pour le rôle: " + personnelDTO.getRolePersonnel());
        }
        personnelDTO.setCodePersonnel(generatedCode);
        
        // Convert DTO to entity and ensure code is set
        Personnel personnel = new Personnel();
        personnel.setCodePersonnel(generatedCode);
        personnel.setNomPersonnel(personnelDTO.getNomPersonnel());
        personnel.setLoginPersonnel(personnelDTO.getLoginPersonnel());
        personnel.setPasswordPersonnel(passwordEncoder.encode(personnelDTO.getPasswordPersonnel()));
        //personnel.setPasswordPersonnel(personnelDTO.getPasswordPersonnel());
        personnel.setSexe(personnelDTO.getSexe());
        if (personnelDTO.getRolePersonnel() != null) {
            personnel.setRolePersonnel(RolePersonnel.valueOf(personnelDTO.getRolePersonnel()));
        }
        
        Personnel savedPersonnel = personnelRepository.save(personnel);
        return personnelMapper.toDTO(savedPersonnel);
    }

    // READ - Récupérer tous les personnels
    public List<PersonnelDTO> getAllPersonnels() {
        List<Personnel> personnels = personnelRepository.findAll();
        return personnels.stream()
                .map(personnelMapper::toDTO)
                .toList();
    }

    //Recuperer un personnel par son role
    public PersonnelDTO getPersonnelByRole(String rolePersonnel){
    Optional<Personnel> personnel = Optional.ofNullable(personnelRepository.findByRolePersonnel(rolePersonnel));
    return personnel.map(personnelMapper::toDTO)
            .orElseThrow(() -> new RuntimeException("Personnel non trouvé avec le role: " + rolePersonnel));
    }


    // READ - Récupérer un personnel par son code
    public PersonnelDTO getPersonnelByCode(String codePersonnel) {
        Optional<Personnel> personnel = personnelRepository.findById(codePersonnel);
        return personnel.map(personnelMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé avec le code: " + codePersonnel));
    }

    // UPDATE - Mettre à jour un personnel
    public PersonnelDTO updatePersonnel(String codePersonnel, PersonnelDTO personnelDTO) {
        Personnel personnel = personnelRepository.findById(codePersonnel)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé avec le code: " + codePersonnel));

        // Mettre à jour les champs
        personnel.setNomPersonnel(personnelDTO.getNomPersonnel());
        personnel.setLoginPersonnel(personnelDTO.getLoginPersonnel());
        personnel.setPasswordPersonnel(personnelDTO.getPasswordPersonnel());
        personnel.setSexe(personnelDTO.getSexe());
        personnel.setRolePersonnel(RolePersonnel.valueOf(personnelDTO.getRolePersonnel()));

        Personnel updatedPersonnel = personnelRepository.save(personnel);
        return personnelMapper.toDTO(updatedPersonnel);
    }

    // DELETE - Supprimer un personnel
    public void deletePersonnel(String codePersonnel) {
        Personnel personnel = personnelRepository.findById(codePersonnel)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé avec le code: " + codePersonnel));
        personnelRepository.delete(personnel);
    }

    // DELETE - Supprimer tous les personnels
    public void deleteAllPersonnels() {
        personnelRepository.deleteAll();
    }

    // Vérifier si un personnel existe
    public boolean personnelExists(String codePersonnel) {
        return personnelRepository.existsById(codePersonnel);
    }

    // Compter le nombre total de personnels
    public long countPersonnels() {
        return personnelRepository.count();
    }
}
