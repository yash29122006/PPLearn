package com.p2plearn.learning.service;

import com.p2plearn.learning.entity.CompetitionEntity;
import com.p2plearn.learning.repository.CompetitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.LocalDate;

@Service
public class CompetitionLifecycleService {
    private final LeaderboardService leaderboardService;
    private final CompetitionRepository competitionRepository;
    private final CompetitionService competitionService;
    private static final ZoneId COMPETITION_ZONE =
            ZoneId.of("Asia/Kolkata");

    public CompetitionLifecycleService(
            CompetitionRepository competitionRepository,
            CompetitionService competitionService,
            LeaderboardService leaderboardService
    ) {
        this.competitionRepository = competitionRepository;
        this.competitionService = competitionService;
        this.leaderboardService = leaderboardService;
    }

    @Transactional
    @Scheduled(
            cron = "0 0 * * * *",
            zone = "Asia/Kolkata"
    )
    public void runCompetitionLifecycle() {

        finalizePreviousCompetitionIfNeeded();

        cleanupExpiredCompetition();

        createNextCompetitionIfNeeded();
    }

    @Transactional
    public void cleanupExpiredCompetition() {

        LocalDateTime now = LocalDateTime.now(COMPETITION_ZONE);

        CompetitionEntity competition =
                competitionRepository
                        .findByStatus(
                                CompetitionEntity.CompetitionStatus
                                        .LEADERBOARD_VISIBLE
                        )
                        .orElse(null);

        if (competition == null) {
            return;
        }

        if (competition.getLeaderboardVisibleUntil() != null
                && !now.isBefore(
                competition.getLeaderboardVisibleUntil()
        )) {

            competitionRepository.delete(competition);
        }
    }

    @Transactional
    public void createNextCompetitionIfNeeded() {

        LocalDate today = LocalDate.now();

        LocalDate weekStart =
                today.with(DayOfWeek.MONDAY);

        LocalDate weekEnd =
                today.with(DayOfWeek.SUNDAY);

        competitionRepository
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

    @Transactional
    public void finalizePreviousCompetitionIfNeeded() {

        LocalDate today = LocalDate.now(COMPETITION_ZONE);

        CompetitionEntity competition =
                competitionRepository
                        .findFirstByStatusAndWeekEndBeforeOrderByWeekStartDesc(
                                CompetitionEntity.CompetitionStatus.ACTIVE,
                                today
                        )
                        .orElse(null);

        if (competition == null) {
            return;
        }

        leaderboardService.generateLeaderboardForCompetition(
                competition
        );

        competition.setStatus(
                CompetitionEntity.CompetitionStatus.LEADERBOARD_VISIBLE
        );

        competition.setLeaderboardVisibleUntil(
                competition.getWeekEnd()
                        .plusDays(2)
                        .atStartOfDay()
        );


        competitionRepository.save(competition);

        leaderboardService.broadcastLeaderboard(
                competition
        );
    }

    @Transactional
    public void testFinalizePreviousCompetition() {
        finalizePreviousCompetitionIfNeeded();
    }
}