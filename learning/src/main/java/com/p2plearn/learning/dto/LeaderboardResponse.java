package com.p2plearn.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class LeaderboardResponse {

    private Integer rank;

    private Long studentId;

    private String studentUsername;

    private Integer totalPoints;

    private LocalDate weekStart;

    private LocalDate weekEnd;
}