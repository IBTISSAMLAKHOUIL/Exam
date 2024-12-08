package com.usermanagement.repository;

import com.usermanagement.entity.OurUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<OurUsers, Integer> {

    boolean existsByEmail(String email);
    Optional<OurUsers> findByEmail(String email);
    OurUsers findByVerificationToken(String token);

}
