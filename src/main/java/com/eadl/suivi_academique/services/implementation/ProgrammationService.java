package com.eadl.suivi_academique.services.implementation;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eadl.suivi_academique.dto.ProgrammationDTO;
import com.eadl.suivi_academique.dto.ProgrammationRequest;
import com.eadl.suivi_academique.entities.Cours;
import com.eadl.suivi_academique.entities.Personnel;
import com.eadl.suivi_academique.entities.Programmation;
import com.eadl.suivi_academique.entities.Salle;
import com.eadl.suivi_academique.mappers.ProgrammationMapper;
import com.eadl.suivi_academique.mappers.ProgrammationReqMapper;
import com.eadl.suivi_academique.repositories.CoursRepository;
import com.eadl.suivi_academique.repositories.PersonnelRepository;
import com.eadl.suivi_academique.repositories.ProgrammationRepository;
import com.eadl.suivi_academique.repositories.SalleRepository;
import com.eadl.suivi_academique.services.interfaces.ProgrammationInterface;
import com.eadl.suivi_academique.utils.StatusProgrammation;

@Service
public class ProgrammationService implements ProgrammationInterface{

	@Autowired
	private ProgrammationRepository programmationRepository;

	@Autowired
	private ProgrammationMapper programmationMapper;

	@Autowired
	private SalleRepository salleRepository;

	@Autowired
	private CoursRepository coursRepository;

	@Autowired
	private PersonnelRepository personnelRepository;

	@Autowired
	private ProgrammationReqMapper ProgrammationReqMapper;



	// CREATE
	public ProgrammationDTO createProgrammation(ProgrammationRequest dto) {

		Salle salle = salleRepository.findByCodeSalle(dto.getCodeSalle())
				.orElseThrow(() -> new RuntimeException("Salle non trouvée avec le code: " + dto.getCodeSalle()));

		// Cours cours = coursRepository.findByCodeCours(dto.getCodeCours())
		// 		.orElseThrow(() -> new RuntimeException("Cours non trouvé avec le code: " + dto.getCodeCours()));

		// Personnel personnelProg = personnelRepository.findByCodePersonnel(dto.getCodePersonnelProg())
		// 		.orElseThrow(() -> new RuntimeException("Salle non trouvée avec le code: " + dto.getCodeSalle()));
		
		// Personnel personnelVal = personnelRepository.findByCodePersonnel(dto.getCodePersonnelVal())
		// 		.orElseThrow(() -> new RuntimeException("Salle non trouvée avec le code: " + dto.getCodeSalle()));
		
		if (salle.getStatusSalle() == null || salle.getStatusSalle().toString().equals("FERMEE")) {
			throw new RuntimeException("La salle avec le code " + salle.getCodeSalle() + " est fermée.");
			
		}
		if (salle.getStatusSalle().toString().equals("OCCUPEE")) {
			throw new RuntimeException("La salle avec le code " + salle.getCodeSalle() + " est occupée.");
			
		}


		Programmation programmation = ProgrammationReqMapper.toEntity(dto);

		Programmation savedProgrammation = programmationRepository.save(programmation);
		return programmationMapper.toDTO(savedProgrammation);

	}

	// READ - all
	public List<ProgrammationDTO> getAllProgrammations() {
		
		List<Programmation> programmations = programmationRepository.findAll();
		return programmationMapper.tDtos(programmations);
	}

	// READ - by id
	public ProgrammationDTO getProgrammationById(int codeProgrammation) {

		Optional<Programmation> programmation = programmationRepository.findById(codeProgrammation);
		return programmation.map(programmationMapper::toDTO)
				.orElseThrow(() -> new RuntimeException("Programmation non trouvée avec le code: " + codeProgrammation));
		
	}

	// UPDATE
	public ProgrammationDTO updateProgrammation(int codeProgrammation, ProgrammationRequest dto) {

		Programmation programmation = programmationRepository.findById(codeProgrammation)
				.orElseThrow(() -> new RuntimeException("Programmation non trouvée avec le code: " + codeProgrammation));
		
		Cours cours = coursRepository.findByCodeCours(dto.getCodeCours())
				.orElseThrow(() -> new RuntimeException("Cours non trouvé avec le code: " + dto.getCodeCours()));

		Personnel personnelProg = personnelRepository.findByCodePersonnel(dto.getCodePersonnelProg())
				.orElseThrow(() -> new RuntimeException("Salle non trouvée avec le code: " + dto.getCodeSalle()));
		
		Personnel personnelVal = personnelRepository.findByCodePersonnel(dto.getCodePersonnelVal())
				.orElseThrow(() -> new RuntimeException("Salle non trouvée avec le code: " + dto.getCodeSalle()));

		// Mettre à jour les champs
		programmation.setNbHeureProgammation(dto.getNbHeureProgammation());

		programmation.setDebutProgammation(dto.getDebutProgammation());

		programmation.setFinProgammation(dto.getFinProgammation());
		
		programmation.setStatusProgrammation(StatusProgrammation.valueOf(dto.getStatusProgrammation()));

		programmation.setCours(cours);

		programmation.setPersonnelProg(personnelProg);

		programmation.setPersonnelVal(personnelVal);

		Programmation updated = programmationRepository.save(programmation);
		
		return programmationMapper.toDTO(updated);

	}

	// DELETE - by id
	public void deleteProgrammation(int codeProgrammation) {
		
	}

	// DELETE - all
	public void deleteAllProgrammations() {
		programmationRepository.deleteAll();
	}
}

	