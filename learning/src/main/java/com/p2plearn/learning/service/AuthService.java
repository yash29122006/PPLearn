package com.p2plearn.learning.service;

import com.p2plearn.learning.dto.LoginRequest;
import com.p2plearn.learning.dto.LoginResponse;
import com.p2plearn.learning.dto.RegisterRequest;
import com.p2plearn.learning.entity.CommunityApplicationEntity;
import com.p2plearn.learning.entity.UserEntity;
import com.p2plearn.learning.repository.CommunityApplicationRepository;
import com.p2plearn.learning.repository.UserRepository;
import com.p2plearn.learning.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CommunityApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            CommunityApplicationRepository applicationRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserEntity register(RegisterRequest request) {

        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Username is already registered"
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        UserEntity user = new UserEntity();

        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );
        user.setRole(UserEntity.Role.STUDENT);
        user.setActive(false);
        user.setTotalPoints(0);

        UserEntity savedUser = userRepository.save(user);

        CommunityApplicationEntity application =
                new CommunityApplicationEntity();

        application.setStudent(savedUser);
        application.setStatus(
                CommunityApplicationEntity.ApplicationStatus.PENDING
        );

        applicationRepository.save(application);

        return savedUser;
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername().trim(),
                                request.getPassword()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        UserEntity user = userRepository
                .findByUsername(userDetails.getUsername())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        )
                );

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getTotalPoints()
        );
    }

    @Transactional(readOnly = true)
    public UserEntity getCurrentUser(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }
}