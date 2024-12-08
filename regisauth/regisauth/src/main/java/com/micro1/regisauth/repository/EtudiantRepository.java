package com.micro1.regisauth.repository;

import com.micro1.regisauth.dto.EtudiantDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EtudiantRepository extends JpaRepository<EtudiantDTO, Long> {
    Optional<EtudiantDTO> findByEmail(String email);
}
