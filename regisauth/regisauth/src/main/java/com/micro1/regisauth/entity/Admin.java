package com.micro1.regisauth.entity;

import com.micro1.regisauth.Enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static com.micro1.regisauth.Enums.Role.ADMINISTRATEUR;

@Getter
@Setter
@Entity
@Table
public class Admin implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Le nom ne peut pas être nul.")
    private String nom;


    @Column(nullable = false, unique = true)
    @NotNull(message = "L'email ne peut pas être nul.")
    @Email(message = "L'email doit être valide.")
    private String email;

    @Column(nullable = false)
    @NotNull(message = "Le mot de passe ne peut pas être nul.")
    @Size(min = 8, message = "Le mot de passe doit avoir au moins 8 caractères.")
    private String motDePasse;

    private Role role = ADMINISTRATEUR;  // Rôle par défaut pour l'administrateur

    public Admin() {
        super();
    }

    // Cette méthode est ignorée pour la persistance dans la base de données
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ADMINISTRATEUR"));
    }

    @Override
    public String getPassword() {
        return super.getMotDePasse();  // Utilisation du mot de passe héritée de Utilisateur
    }

    @Override
    public String getUsername() {
        return super.getEmail();  // Utilisation de l'email comme nom d'utilisateur
    }
}
