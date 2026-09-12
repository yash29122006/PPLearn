package com.p2plearn.learning.controller;

import com.p2plearn.learning.dto.AttemptResponse;
import com.p2plearn.learning.service.AttemptService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/attempts")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('STUDENT')")
public class AttemptController {

    private final AttemptService attemptService;

    public AttemptController(
            AttemptService attemptService
    ) {
        this.attemptService = attemptService;
    }

    @PostMapping("/{questionId}/start")
    public ResponseEntity<?> startAttempt(
            @PathVariable Long questionId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            Jwt jwt
    ) {

        try {

            AttemptResponse response =
                    attemptService.startAttempt(
                            questionId,
                            jwt.getSubject()
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

    @PostMapping("/{questionId}/finish")
    public ResponseEntity<?> finishAttempt(
            @PathVariable Long questionId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            Jwt jwt
    ) {

        try {

            AttemptResponse response =
                    attemptService.finishAttempt(
                            questionId,
                            jwt.getSubject()
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

    @GetMapping("/{questionId}/me")
    public ResponseEntity<?> getMyAttempt(
            @PathVariable Long questionId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        try {
            return ResponseEntity.ok(
                    attemptService.getMyAttempt(
                            questionId,
                            jwt.getSubject()
                    )
            );
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    exception.getMessage()
                            )
                    );
        }
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<?> getQuestionAttempts(
            @PathVariable Long questionId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        try {
            return ResponseEntity.ok(
                    attemptService.getQuestionAttempts(
                            questionId,
                            jwt.getSubject()
                    )
            );
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", exception.getMessage()));
        }
    }
}