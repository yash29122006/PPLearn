package com.p2plearn.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class QuestionListResponse {

    private Long questionId;

    private Long postedById;
    private String postedByUsername;

    private String title;
    private String description;
    private String leetcodeUrl;

    private LocalDate postedDate;
    private LocalDateTime postedAt;

    private boolean ownQuestion;

    private boolean attempted;

    private String attemptStatus;

    private Integer attemptPoints;
}