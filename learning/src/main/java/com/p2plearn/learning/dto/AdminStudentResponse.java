package com.p2plearn.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminStudentResponse {

    private Long studentId;

    private String username;

    private String email;

    private Integer totalPoints;

    private LocalDateTime createdAt;
}