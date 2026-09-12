package com.p2plearn.learning.controller;

import com.p2plearn.learning.dto.QuestionRequest;
import com.p2plearn.learning.dto.QuestionResponse;
import com.p2plearn.learning.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Map;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/questions")
@SecurityRequirement(name = "bearerAuth")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(
            QuestionService questionService
    ) {
        this.questionService = questionService;
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping
    public ResponseEntity<?> postQuestion(
            @Valid @RequestBody QuestionRequest request,
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            Jwt jwt
    ) {

        try {

            QuestionResponse response =
                    questionService.postQuestion(
                            request,
                            jwt.getSubject()
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            exception.getMessage()
                    ));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getQuestions(
            @org.springframework.security.core.annotation.AuthenticationPrincipal
            Jwt jwt
    ) {

        try {

            return ResponseEntity.ok(
                    questionService
                            .getCurrentCompetitionQuestions(
                                    jwt.getSubject()
                            )
            );

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