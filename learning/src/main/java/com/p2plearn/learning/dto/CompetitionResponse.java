package com.p2plearn.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CompetitionResponse {

    private Long competitionId;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private String status;
}