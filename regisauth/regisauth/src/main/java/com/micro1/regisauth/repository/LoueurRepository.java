package com.micro1.regisauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoueurRepository extends JpaRepository<Loueur, Long>{
    Optional<Loueur> findByEmail(String email);
}


