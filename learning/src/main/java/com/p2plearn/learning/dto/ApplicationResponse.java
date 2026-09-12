package com.p2plearn.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApplicationResponse {

    private Long applicationId;

    private Long studentId;

    private String username;

    private String email;

    private String status;

    private LocalDateTime createdAt;
}
