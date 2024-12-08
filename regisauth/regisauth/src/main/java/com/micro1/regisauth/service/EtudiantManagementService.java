package com.micro1.regisauth.service;



import com.micro1.regisauth.dto.EtudiantDTO;
import com.micro1.regisauth.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class EtudiantManagementService {

    @Autowired
    private EtudiantRepository etudiantRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public EtudaintDTO register(EtudaintDTO registrationRequest){
        EtudaintDTO etudiantDTO = new EtudaintDTO();

        try {
            EtudiantDTO etudiantDTO = new EtudiantDTO();
            etudiantDTO.setEmail(registrationRequest.getEmail());
            etudiantDTO.setCity(registrationRequest.getCity());
            etudiantDTO.setRole(registrationRequest.getRole());
            etudiantDTO.setNom(registrationRequest.getName());
            etudiantDTO.setMotDePasse(passwordEncoder.encode(registrationRequest.getMotDePasse()));
            etudiantDTO etudiantResult = EtudiantRepository.save(EtudiantDTO);
            if (etudiantResult.getId()>0) {
                etudiantDTO.setEtudiantDTO((etudiantResult));
                etudiantDTO.setMessage("User Saved Successfully");
                etudiantDTO.setStatusCode(200);
            }

        }catch (Exception e){
            etudiantDTO.setStatusCode(500);
            etudiantDTO.seterror(e.getMessage());
        }
        return etudiantDTO;
    }


    public EtudiantDTO login(EtudiantDTO loginRequest){
        EtudiantDTO response = new EtudiantDTO();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getMotDePasse()));
            var user = etudiantRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
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





    public EtudiantDTO refreshToken(EtudiantDTO refreshTokenReqiest){
        EtudiantDTO response = new EtudiantDTO();
        try{
            String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
            Etudiant users = etudiantRepository.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenReqiest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            return response;

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }


    public EtudiantDTO getAllUsers() {
        EtudiantDTO etudiantDTO = new EtudiantDTO();

        try {
            List<Etudiant> result = etudiantRepository.findAll();
            if (!result.isEmpty()) {
                etudiantDTO.setEtudiantDTOList();List(result);
                etudiantDTO.setStatusCode(200);
                etudiantDTO.setMessage("Successful");
            } else {
                etudiantDTO.setStatusCode(404);
                etudiantDTO.setMessage("No users found");
            }
            return etudiantDTO;
        } catch (Exception e) {
            etudiantDTO.setStatusCode(500);
            etudiantDTO.setMessage("Error occurred: " + e.getMessage());
            return etudiantDTO;
        }
    }


    public ReqRes getUsersById(Integer id) {
        ReqRes reqRes = new ReqRes();
        try {
            Utilisateur usersById = utilisateurRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setUtilisateur(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }


    public ReqRes deleteUser(Integer userId) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Utilisateur> userOptional = utilisateurRepository.findById(userId);
            if (userOptional.isPresent()) {
                utilisateurRepository.deleteById(userId);
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

    public ReqRes updateUser(Integer userId, Utilisateur updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Utilisateur> userOptional = utilisateurRepository.findById(userId);
            if (userOptional.isPresent()) {
                Utilisateur existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setRole(updatedUser.getRole());

                // Check if password is present in the request
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    // Encode the password and update it
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                Utilisateur savedUser = utilisateurRepository.save(existingUser);
                reqRes.setUtilisateur(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }


    public ReqRes getMyInfo(String email){
        ReqRes reqRes = new ReqRes();
        try {
            Optional<Utilisateur> userOptional = utilisateurRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                reqRes.setUtilisateur(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }

        }catch (Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;

    }
}