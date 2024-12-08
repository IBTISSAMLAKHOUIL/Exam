package com.micro1.regisauth.dto;

import java.util.List;

public class AdminDTO {

    private Long id;
    private String nom;
    private String email;
    private String motDePasse;
    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private AdminDTO adminDTO;
    private List<AdminDTO> adminDTOList;
}
