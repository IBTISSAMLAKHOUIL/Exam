package com.usermanagement.service;

import com.usermanagement.service.JWTUtils;
import com.usermanagement.dto.ReqRes;
import com.usermanagement.entity.OurUsers;
import com.usermanagement.repository.UsersRepo;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersManagementService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;




    // Enregistrement de l'etudiant
    public ReqRes registeretudiant(@Valid ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();

        try {
            // Vérification de l'email
            if (usersRepo.existsByEmail(registrationRequest.getEmail())) {
                resp.setStatusCode(400);  // Mauvaise requête
                resp.setMessage("L'email est déjà utilisé.");
                return resp;
            }

            // Création de l'etudiant si l'email est unique
            OurUsers ourUser = new OurUsers();
            ourUser.setName(registrationRequest.getName());
            ourUser.setPrenom(registrationRequest.getPrenom());
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            ourUser.setRole(registrationRequest.getRole());

            // Génération du token de vérification
            String token = generateVerificationToken();
            ourUser.setVerificationToken(token);

            // Sauvegarde de l'utilisateur avec le token
            OurUsers ourUsersResult = usersRepo.save(ourUser);

            if (ourUsersResult.getId() > 0) {
                // Envoi de l'email de confirmation
                sendConfirmationEmail(ourUser.getEmail(), token);

                resp.setOurUsers(ourUsersResult);
                resp.setMessage("Utilisateur enregistré avec succès. Un email de confirmation a été envoyé.");
                resp.setStatusCode(200);
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    // Méthode pour envoyer un email de confirmation
    public void sendConfirmationEmail(String email, String token) {
        String subject = "Confirmez votre adresse email";
        String text = "Veuillez cliquer sur le lien suivant pour confirmer votre adresse email :\n" +
                "http://localhost:1010/confirm-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);

        // Envoie de l'email
        mailSender.send(message);
    }

    // Méthode utilitaire pour générer un token de vérification
    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }
    public ReqRes confirmEmail(String token) {
        ReqRes response = new ReqRes();
        try {
            // Chercher l'utilisateur par le token
            OurUsers user = usersRepo.findByVerificationToken(token);
            if (user == null) {
                response.setStatusCode(404);
                response.setMessage("Token invalide ou expiré.");
                return response;
            }

            // Marquer l'utilisateur comme vérifié
            user.setVerified(true);
            user.setVerificationToken(null); // Token inutilisable après vérification
            usersRepo.save(user);

            response.setStatusCode(200);
            response.setMessage("Email vérifié avec succès.");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError("Erreur interne : " + e.getMessage());
        }
        return response;
    }


    public ReqRes registerloueur(@Valid ReqRes registrationRequest){
        ReqRes resp = new ReqRes();

        try {
            OurUsers ourUser = new OurUsers();
            ourUser.setName(registrationRequest.getName());
            ourUser.setPrenom(registrationRequest.getPrenom());
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setSexe(registrationRequest.getSexe());
            ourUser.setAdresse(registrationRequest.getAdresse());
            ourUser.setPhone(registrationRequest.getPhone());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword())); // Encodage du mot de passe
            ourUser.setRole(registrationRequest.getRole());
            OurUsers ourUsersResult = usersRepo.save(ourUser);
            if (ourUsersResult.getId() > 0) {
                resp.setOurUsers((ourUsersResult));
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }

        }catch (Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes registeradmin(ReqRes registrationRequest){
        ReqRes resp = new ReqRes();

        try {
            OurUsers ourUser = new OurUsers();
            ourUser.setName(registrationRequest.getName());
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword())); // Encodage du mot de passe
            ourUser.setRole(registrationRequest.getRole());
            OurUsers ourUsersResult = usersRepo.save(ourUser);
            if (ourUsersResult.getId() > 0) {
                resp.setOurUsers((ourUsersResult));
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }

        }catch (Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes login(ReqRes loginRequest){
        ReqRes response = new ReqRes();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));
            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public ReqRes refreshToken(ReqRes refreshTokenReqiest) {
        ReqRes response = new ReqRes();
        try{
            String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
            OurUsers users = usersRepo.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenReqiest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            } else {
                response.setStatusCode(400);
                response.setMessage("Token invalide.");
            }
        } catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("Erreur lors du rafraîchissement du token : " + e.getMessage());
        }
        return response;
    }



    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();

        try {
            List<OurUsers> result = usersRepo.findAll();
            if (!result.isEmpty()) {
                reqRes.setOurUsersList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }

    public ReqRes getMyInfo(String email) {
        // Récupérer l'utilisateur, s'il existe
        Optional<OurUsers> optionalUser = usersRepo.findByEmail(email);

        // Créer un objet ReqRes pour préparer la réponse
        ReqRes response = new ReqRes();

        // Vérifier si l'utilisateur est présent
        if (!optionalUser.isPresent()) {
            // Si l'utilisateur n'est pas trouvé, retour d'une erreur dans le ReqRes
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setError("User not found");
            response.setMessage("No user found with the provided email.");
            return response;
        }

        // Si l'utilisateur est trouvé, on le récupère de l'Optional
        OurUsers user = optionalUser.get();

        // Remplir l'objet ReqRes avec les informations de l'utilisateur
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("User profile retrieved successfully");
        response.setOurUsers(user); // Associe l'utilisateur trouvé à l'objet ReqRes

        return response;
    }



    public ReqRes getUsersById(Integer id) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> usersById = usersRepo.findById(id);
            if (usersById.isPresent()) {
                reqRes.setOurUsers(usersById.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("Utilisateur trouvé avec succès.");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("Utilisateur avec l'ID '" + id + "' non trouvé.");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes deleteUser(Integer userId) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                usersRepo.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes updateUser(Integer userId, OurUsers updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                OurUsers existingUser = userOptional.get();

                if (updatedUser.getName() != null) existingUser.setName(updatedUser.getName());
                if (updatedUser.getPrenom() != null) existingUser.setPrenom(updatedUser.getPrenom());
                if (updatedUser.getEmail() != null) existingUser.setEmail(updatedUser.getEmail());
                if (updatedUser.getPhone() != null) existingUser.setPhone(updatedUser.getPhone());
                if (updatedUser.getRole() != null) existingUser.setRole(updatedUser.getRole());
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                usersRepo.save(existingUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Utilisateur mis à jour avec succès.");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("Utilisateur avec l'ID '" + userId + "' non trouvé.");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }
        return reqRes;
    }
}
