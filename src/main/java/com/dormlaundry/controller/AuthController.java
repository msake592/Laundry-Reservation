package com.dormlaundry.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dormlaundry.dto.AuthResponse;
import com.dormlaundry.dto.RegisterRequest;
import com.dormlaundry.model.Role;
import com.dormlaundry.model.User;
import com.dormlaundry.repository.UserRepository;
import com.dormlaundry.security.JwtService;
import com.dormlaundry.service.CustomUserDetailsService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService customUserDetailsService,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Username already exists"));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = request.getRole() != null ? request.getRole() : Role.USER;
        user.setRole(role);

        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "User registered successfully",
                        "username", user.getUsername(),
                        "role", user.getRole()
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name()
        ));
        
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "role", user.getRole()
        ));
    }
}
