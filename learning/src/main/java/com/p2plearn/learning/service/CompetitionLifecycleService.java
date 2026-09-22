package com.p2plearn.learning.service;

import com.p2plearn.learning.entity.CompetitionEntity;
import com.p2plearn.learning.entity.UserEntity;
import com.p2plearn.learning.repository.CompetitionRepository;
import com.p2plearn.learning.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class CompetitionLifecycleService {

    private final LeaderboardService leaderboardService;
    private final CompetitionRepository competitionRepository;
    private final CompetitionService competitionService;
    private final UserRepository userRepository;

    private static final ZoneId COMPETITION_ZONE =
            ZoneId.of("Asia/Kolkata");

    public CompetitionLifecycleService(
            CompetitionRepository competitionRepository,
            CompetitionService competitionService,
            LeaderboardService leaderboardService,
            UserRepository userRepository
    ) {
        this.competitionRepository = competitionRepository;
        this.competitionService = competitionService;
        this.leaderboardService = leaderboardService;
        this.userRepository = userRepository;
    }

    /**
     * Runs every hour.
     *
     * Handles:
     * - finalizing the previous competition
     * - resetting weekly points
     * - creating the new competition
     * - removing expired old competitions
     */
    @Transactional
    @Scheduled(
            cron = "0 0 * * * *",
            zone = "Asia/Kolkata"
    )
    public void runCompetitionLifecycle() {

        finalizePreviousCompetitionIfNeeded();

        resetWeeklyPointsIfNeeded();

        createNextCompetitionIfNeeded();

        cleanupExpiredCompetitions();
    }

    /**
     * Finalizes the most recent ACTIVE competition whose week has ended.
     *
     * The finalized leaderboard remains visible throughout the
     * following week.
     */
    @Transactional
    public void finalizePreviousCompetitionIfNeeded() {

        LocalDate today =
                LocalDate.now(COMPETITION_ZONE);

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

        // Calculate and permanently store the Top 3.
        leaderboardService.generateLeaderboardForCompetition(
                competition
        );

        competition.setStatus(
                CompetitionEntity.CompetitionStatus.LEADERBOARD_VISIBLE
        );

        /*
         * Keep the finalized leaderboard visible for the
         * entire following week.
         *
         * Example:
         *
         * Previous week:
         * Monday 21 Sep -> Sunday 27 Sep
         *
         * Visible until:
         * Monday 5 Oct 00:00
         *
         * This means it remains visible throughout
         * Monday 28 Sep -> Sunday 4 Oct.
         */
        competition.setLeaderboardVisibleUntil(
                competition.getWeekEnd()
                        .plusDays(8)
                        .atStartOfDay()
        );

        competitionRepository.save(competition);
    }

    /**
     * Resets the current-week points after a competition
     * has been finalized.
     *
     * totalPoints is used as the student's CURRENT WEEK
     * points. The finalized weekly score is already stored
     * separately in the leaderboard table.
     */
    @Transactional
    public void resetWeeklyPointsIfNeeded() {

        LocalDate today =
                LocalDate.now(COMPETITION_ZONE);

        LocalDate currentWeekStart =
                today.with(DayOfWeek.MONDAY);

        boolean currentWeekExists =
                competitionRepository
                        .findByWeekStart(currentWeekStart)
                        .isPresent();

        if (currentWeekExists) {
            return;
        }

        /*
         * No competition exists for the new week yet.
         *
         * Therefore this is the week-transition point.
         * Reset active students' current-week points.
         */
        userRepository
                .findByRoleAndActiveTrueOrderByCreatedAtAsc(
                        UserEntity.Role.STUDENT
                )
                .forEach(student -> {
                    student.setTotalPoints(0);
                    userRepository.save(student);
                });
    }

    /**
     * Creates the competition for the current Monday-Sunday week.
     */
    @Transactional
    public void createNextCompetitionIfNeeded() {

        LocalDate today =
                LocalDate.now(COMPETITION_ZONE);

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

                    return competitionRepository.save(
                            competition
                    );
                });
    }

    /**
     * Deletes finalized competitions whose visibility period
     * has expired.
     *
     * We use findAll() here because there can temporarily be
     * two LEADERBOARD_VISIBLE competitions during the Monday
     * transition:
     *
     * - the old previous week
     * - the newly finalized week
     *
     * Only the expired one should be deleted.
     */
    @Transactional
    public void cleanupExpiredCompetitions() {

        LocalDateTime now =
                LocalDateTime.now(COMPETITION_ZONE);

        competitionRepository
                .findAll()
                .stream()
                .filter(competition ->
                        competition.getStatus()
                                == CompetitionEntity.CompetitionStatus
                                .LEADERBOARD_VISIBLE
                )
                .filter(competition ->
                        competition.getLeaderboardVisibleUntil() != null
                )
                .filter(competition ->
                        !now.isBefore(
                                competition.getLeaderboardVisibleUntil()
                        )
                )
                .forEach(competitionRepository::delete);
    }

    /**
     * Manual testing method.
     */
    @Transactional
    public void testFinalizePreviousCompetition() {

        finalizePreviousCompetitionIfNeeded();
    }
}