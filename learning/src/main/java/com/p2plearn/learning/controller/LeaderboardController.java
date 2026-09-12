package com.p2plearn.learning.controller;

import com.p2plearn.learning.dto.LeaderboardResponse;
import com.p2plearn.learning.service.LeaderboardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@SecurityRequirement(name = "bearerAuth")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(
            LeaderboardService leaderboardService
    ) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<LeaderboardResponse>> getLeaderboard() {

        return ResponseEntity.ok(
                leaderboardService.getCurrentLeaderboard()
        );
    }

    @GetMapping("/preview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LeaderboardResponse>> previewLeaderboard() {

        return ResponseEntity.ok(
                leaderboardService.previewCurrentLeaderboard()
        );
    }
}