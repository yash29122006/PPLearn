package com.p2plearn.learning.controller;

import com.p2plearn.learning.dto.CompetitionResponse;
import com.p2plearn.learning.entity.CompetitionEntity;
import com.p2plearn.learning.repository.CompetitionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/competition")
@PreAuthorize("hasRole('STUDENT')")
public class CompetitionController {

    private final CompetitionRepository competitionRepository;

    public CompetitionController(
            CompetitionRepository competitionRepository
    ) {
        this.competitionRepository = competitionRepository;
    }

    @GetMapping("/current")
    public ResponseEntity<CompetitionResponse> getCurrentCompetition(
            @AuthenticationPrincipal Jwt jwt
    ) {

        CompetitionEntity competition =
                competitionRepository
                        .findFirstByStatusOrderByWeekStartDesc(
                                CompetitionEntity.CompetitionStatus.ACTIVE
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "No active competition found"
                                )
                        );

        return ResponseEntity.ok(
                new CompetitionResponse(
                        competition.getId(),
                        competition.getWeekStart(),
                        competition.getWeekEnd(),
                        competition.getStatus().name()
                )
        );
    }
}