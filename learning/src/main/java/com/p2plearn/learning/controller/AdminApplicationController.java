package com.p2plearn.learning.controller;

import com.p2plearn.learning.dto.ApplicationDecisionRequest;
import com.p2plearn.learning.dto.ApplicationResponse;
import com.p2plearn.learning.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.Map;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/admin/applications")
public class AdminApplicationController {

    private final ApplicationService applicationService;

    public AdminApplicationController(
            ApplicationService applicationService
    ) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getPendingApplications() {

        return ResponseEntity.ok(
                applicationService.getPendingApplications()
        );
    }

    @PatchMapping("/{applicationId}")
    public ResponseEntity<?> decideApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody ApplicationDecisionRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {

        try {
            String adminUsername = jwt.getSubject();

            ApplicationResponse response =
                    applicationService.decideApplication(
                            applicationId,
                            request,
                            adminUsername
                    );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            exception.getMessage()
                    ));
        }
    }
}