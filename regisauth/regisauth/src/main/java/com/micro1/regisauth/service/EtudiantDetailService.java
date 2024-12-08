package com.micro1.regisauth.service;

import com.micro1.regisauth.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EtudiantDetailService implements UserDetailsService {

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return etudiantRepository.findByEmail(username).orElseThrow();
    }
}
