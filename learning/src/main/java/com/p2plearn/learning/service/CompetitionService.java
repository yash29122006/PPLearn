package com.p2plearn.learning.service;

import com.p2plearn.learning.entity.CompetitionEntity;
import com.p2plearn.learning.repository.CompetitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZoneId;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private static final ZoneId COMPETITION_ZONE =
            ZoneId.of("Asia/Kolkata");
    public CompetitionService(
            CompetitionRepository competitionRepository
    ) {
        this.competitionRepository = competitionRepository;
    }

    @Transactional
    public CompetitionEntity getOrCreateCurrentCompetition() {

        LocalDate today =
                LocalDate.now(COMPETITION_ZONE);

        LocalDate weekStart =
                today.with(DayOfWeek.MONDAY);

        LocalDate weekEnd =
                today.with(DayOfWeek.SUNDAY);

        return competitionRepository
                .findByWeekStart(weekStart)
                .orElseGet(() -> {

                    CompetitionEntity competition =
                            new CompetitionEntity();

                    competition.setWeekStart(weekStart);
                    competition.setWeekEnd(weekEnd);
                    competition.setStatus(
                            CompetitionEntity.CompetitionStatus.ACTIVE
                    );

                    return competitionRepository.save(competition);
                });
    }
}