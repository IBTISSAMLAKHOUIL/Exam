package com.micro1.regisauth.entity;

import com.micro1.regisauth.Enums.Sexe;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import com.micro1.regisauth.Enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import static com.micro1.regisauth.Enums.Role.ADMINISTRATEUR;
import static com.micro1.regisauth.Enums.Role.ETUDIANT;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table
public class Etudiant implements UserDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        @NotNull(message = "Le nom ne peut pas être nul.")
        private String nom;

        @Column(nullable = false)
        @NotNull(message = "Le prénom ne peut pas être nul.")
        private String prenom;

        @Column(nullable = false, unique = true)
        @NotNull(message = "L'email ne peut pas être nul.")
        @Email(message = "L'email doit être valide.")
        private String email;

        @Column(nullable = false)
        @NotNull(message = "Le mot de passe ne peut pas être nul.")
        @Size(min = 8, message = "Le mot de passe doit avoir au moins 8 caractères.")
        private String motDePasse;

        @Column(nullable = false)
        @NotNull(message = "Le numéro de téléphone ne peut pas être nul.")
        @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Le numéro de téléphone doit être valide.")
        private String numeroTelephone;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        @NotNull(message = "Le sexe ne peut pas être nul.")
        private Sexe sexe;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        @NotNull(message = "Le rôle ne peut pas être nul.")
        private Role role = ETUDIANT;

        @Column(nullable = false)
        private Boolean profilComplet = false;

        private Integer age;
        private String caracterPersonnel;
        private String styleDeVie;
        private String habitudesAlimentaires;
        private String ecole;
        private String ville;
        private String exigence;
        private LocalDate disponibilite;
        private Integer matchingScore;


        @Override
        public boolean isAccountNonExpired() {
                return true;
        }

        @Override
        public boolean isAccountNonLocked() {
                return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
                return true;
        }

        @Override
        public boolean isEnabled() {
                return true;
        }
}



        public Etudiant() {
                super();
        }

        // Cette méthode ne doit pas être une propriété persistée dans la base de données.
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority("ETUDIANT"));
        }

        @Override
        public String getPassword() {
                return super.getMotDePasse(); // Utilisation du mot de passe hérité de Utilisateur
        }

        @Override
        public String getUsername() {
                return super.getEmail(); // Utilisation de l'email comme nom d'utilisateur
        }
}