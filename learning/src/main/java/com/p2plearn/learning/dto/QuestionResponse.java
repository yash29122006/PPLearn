package com.p2plearn.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class QuestionResponse {

    private Long questionId;
    private Long competitionId;

    private Long postedById;
    private String postedByUsername;

    private String title;
    private String description;
    private String leetcodeUrl;

    private LocalDate postedDate;
    private LocalDateTime postedAt;

    private Integer pointsAwarded;
}