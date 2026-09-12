package com.p2plearn.learning.controller;

import com.p2plearn.learning.dto.LoginRequest;
import com.p2plearn.learning.dto.LoginResponse;
import com.p2plearn.learning.dto.RegisterRequest;
import com.p2plearn.learning.entity.UserEntity;
import com.p2plearn.learning.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        try {
            UserEntity user = authService.register(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message",
                            "Registration successful. Waiting for admin approval.",
                            "userId",
                            user.getId(),
                            "username",
                            user.getUsername(),
                            "status",
                            "PENDING"
                    ));

        } catch (IllegalArgumentException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            exception.getMessage()
                    ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request
    ) {

        try {
            LoginResponse response = authService.login(request);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException exception) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Invalid username or password"
                    ));

        } catch (AuthenticationException exception) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "Authentication failed"
                    ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(
            @AuthenticationPrincipal Jwt jwt
    ) {

        UserEntity user =
                authService.getCurrentUser(jwt.getSubject());

        return ResponseEntity.ok(
                Map.of(
                        "userId", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "role", user.getRole().name(),
                        "totalPoints", user.getTotalPoints()
                )
        );
    }
}