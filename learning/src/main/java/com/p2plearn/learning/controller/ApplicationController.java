package com.p2plearn.learning.controller;

import com.p2plearn.learning.dto.ApplicationResponse;
import com.p2plearn.learning.service.ApplicationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(
            ApplicationService applicationService
    ) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<?> applyToCommunity(
            @AuthenticationPrincipal Jwt jwt
    ) {

        try {
            String studentUsername = jwt.getSubject();

            ApplicationResponse response =
                    applicationService.applyToCommunity(
                            studentUsername
                    );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(java.util.Map.of(
                            "message",
                            exception.getMessage()
                    ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyApplication(
            @AuthenticationPrincipal Jwt jwt
    ) {

        try {
            String studentUsername = jwt.getSubject();

            ApplicationResponse response =
                    applicationService.getMyApplication(
                            studentUsername
                    );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(java.util.Map.of(
                            "message",
                            exception.getMessage()
                    ));
        }
    }
}