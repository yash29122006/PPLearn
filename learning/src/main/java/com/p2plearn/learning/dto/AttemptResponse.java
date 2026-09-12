package com.p2plearn.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AttemptResponse {

    private Long attemptId;

    private Long questionId;

    private Long studentId;

    private String studentUsername;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private String status;

    private Integer pointsEarned;

    private Integer bonusPoints;

    private Integer totalPoints;
}