package com.p2plearn.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class WeeklyLeaderboardResponse {

    private List<LeaderboardResponse> currentWeek;
    private List<LeaderboardResponse> previousWeek;
}