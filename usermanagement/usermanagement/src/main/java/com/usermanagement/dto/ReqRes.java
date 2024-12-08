package com.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.usermanagement.entity.OurUsers;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String prenom;
    private String city;
    private String role;
    private String email;
    private String password;
    private String sexe;
    private String adresse;
    private String phone;
    private OurUsers ourUsers;
    private List<OurUsers> ourUsersList;
    private String verificationToken;
    private boolean verified;

}