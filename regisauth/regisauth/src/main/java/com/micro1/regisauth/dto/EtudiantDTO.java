package com.micro1.regisauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.micro1.regisauth.Enums.Role;
import com.micro1.regisauth.Enums.Sexe;
import com.micro1.regisauth.entity.Etudiant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class EtudiantDTO extends UtilisateurDTO {
    private Integer age;
    private String caracterPersonnel;
    private String styleDeVie;
    private String habitudesAlimentaires;
    private String ecole;
    private String ville;
    private String exigence;
    private LocalDate disponibilite;
    private Integer matchingScore;
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String numeroTelephone;
    private Sexe sexe;
    private Role role;
    private Boolean profilComplet;
    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private EtudiantDTO etudiantDTO;
    private List<EtudiantDTO> etudiantDTOList;
}

