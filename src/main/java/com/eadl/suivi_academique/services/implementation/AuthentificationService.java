package com.eadl.suivi_academique.services.implementation;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eadl.suivi_academique.config.JwtUtil;
import com.eadl.suivi_academique.dto.AuthRequest;
import com.eadl.suivi_academique.dto.AuthResponse;
import com.eadl.suivi_academique.dto.PersonnelDTO;
import com.eadl.suivi_academique.entities.Personnel;
import com.eadl.suivi_academique.repositories.PersonnelRepository;
import com.eadl.suivi_academique.services.interfaces.AuthentificationInterface;
import com.eadl.suivi_academique.utils.CodeGenerator;
import com.eadl.suivi_academique.utils.RolePersonnel;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthentificationService implements AuthentificationInterface {

    private final PersonnelRepository personnelRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtService;
    private final AuthenticationManager authenticationManager;
    private final CodeGenerator codeGenerator;


    public AuthResponse authenticate(AuthRequest request){

        
        Personnel p = personnelRepository.findByLoginPersonnel(request.getLogin())
                 .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

        
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );

        String jwtToken = jwtService.generateToken(p);

        return AuthResponse.builder()
                .token(jwtToken)
                .codePersonnel(p.getCodePersonnel())
                .nomPersonnel(p.getNomPersonnel())
                .rolePersonnel(p.getRolePersonnel().name())
                .build();
    }

    public AuthResponse register(PersonnelDTO personnelDTO){

        if (personnelDTO.getPasswordPersonnel() == null || personnelDTO.getPasswordPersonnel().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
        }

        // Generate code based on role
        String generatedCode = codeGenerator.generate(personnelDTO.getRolePersonnel());
        if (generatedCode == null) {
            throw new RuntimeException("Impossible de générer un code pour le rôle: " + personnelDTO.getRolePersonnel());
        }
        personnelDTO.setCodePersonnel(generatedCode);

        // Convert DTO to entity
        Personnel personnel = new Personnel();
        personnel.setCodePersonnel(generatedCode);
        personnel.setNomPersonnel(personnelDTO.getNomPersonnel());
        personnel.setLoginPersonnel(personnelDTO.getLoginPersonnel());
        personnel.setSexe(personnelDTO.getSexe());

        // Secure role conversion
        try {
            personnel.setRolePersonnel(RolePersonnel.valueOf(personnelDTO.getRolePersonnel().toUpperCase()));
        } catch (Exception e){
            throw new IllegalArgumentException("Rôle invalide : " + personnelDTO.getRolePersonnel());
        }

        // Encode password
        personnel.setPasswordPersonnel(passwordEncoder.encode(personnelDTO.getPasswordPersonnel()));

        // Save
        Personnel savedPersonnel = personnelRepository.save(personnel);

        // Generate JWT
        String jwtToken = jwtService.generateToken(savedPersonnel);

        return AuthResponse.builder()
                .token(jwtToken)
                .codePersonnel(savedPersonnel.getCodePersonnel())
                .nomPersonnel(savedPersonnel.getNomPersonnel())
                .rolePersonnel(savedPersonnel.getRolePersonnel().name())
                .build();
    }


}

