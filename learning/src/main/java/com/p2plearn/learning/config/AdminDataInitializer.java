package com.p2plearn.learning.config;

import com.p2plearn.learning.entity.UserEntity;
import com.p2plearn.learning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminDataInitializer {

    @Value("${ADMIN_USERNAME}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Bean
    CommandLineRunner createAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            if (userRepository.existsByUsername(adminUsername)) {

                UserEntity existingUser =
                        userRepository.findByUsername(adminUsername)
                                .orElseThrow();

                if (existingUser.getRole() != UserEntity.Role.ADMIN) {
                    throw new IllegalStateException(
                            "ADMIN_USERNAME already belongs to a non-admin user"
                    );
                }

                return;
            }

            UserEntity admin = new UserEntity();

            admin.setUsername(adminUsername);
            admin.setEmail(adminUsername + "@peerlearn.local");
            admin.setPasswordHash(
                    passwordEncoder.encode(adminPassword)
            );
            admin.setRole(UserEntity.Role.ADMIN);
            admin.setActive(true);
            admin.setTotalPoints(0);

            userRepository.save(admin);

            System.out.println(
                    "Initial admin account created successfully."
            );
        };
    }
}