package com.dormlaundry.service;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.dormlaundry.repository.UserRepository;
import com.dormlaundry.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}