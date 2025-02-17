package com.vaccination.BE.configuration.jwt;

import com.vaccination.BE.entity.VaccineEmployee;
import com.vaccination.BE.repository.EmployeeRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private EmployeeRepository userRepository;

    public CustomUserDetailsService(EmployeeRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        VaccineEmployee user = userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found with username or email "+ username));

        Set<GrantedAuthority> authorities = user
                .getRoles()
                .stream()
                .map((role -> new SimpleGrantedAuthority(role.getName()))).collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),authorities);
    }
}