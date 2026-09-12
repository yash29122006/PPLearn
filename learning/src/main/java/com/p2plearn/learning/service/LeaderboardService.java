package com.p2plearn.learning.service;
import com.p2plearn.learning.dto.LeaderboardResponse;
import com.p2plearn.learning.entity.AttemptEntity;
import com.p2plearn.learning.entity.CompetitionEntity;
import com.p2plearn.learning.entity.LeaderboardEntity;
import com.p2plearn.learning.entity.QuestionEntity;
import com.p2plearn.learning.entity.UserEntity;
import com.p2plearn.learning.repository.AttemptRepository;
import com.p2plearn.learning.repository.CompetitionRepository;
import com.p2plearn.learning.repository.LeaderboardRepository;
import com.p2plearn.learning.repository.QuestionRepository;
import com.p2plearn.learning.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeaderboardService {

    private final WebSocketBroadcastService webSocketBroadcastService;
    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;
    private final CompetitionRepository competitionRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;
    private final CompetitionService competitionService;

    public LeaderboardService(
            LeaderboardRepository leaderboardRepository,
            UserRepository userRepository,
            CompetitionRepository competitionRepository,
            AttemptRepository attemptRepository,
            QuestionRepository questionRepository,
            CompetitionService competitionService,
            WebSocketBroadcastService webSocketBroadcastService
    ) {
        this.leaderboardRepository = leaderboardRepository;
        this.userRepository = userRepository;
        this.competitionRepository = competitionRepository;
        this.attemptRepository = attemptRepository;
        this.questionRepository = questionRepository;
        this.competitionService = competitionService;
        this.webSocketBroadcastService = webSocketBroadcastService;
    }

    @Transactional
    public void generateCurrentLeaderboard() {

        CompetitionEntity competition =
                competitionService.getOrCreateCurrentCompetition();

        generateLeaderboardForCompetition(competition);
    }

    @Transactional(readOnly = true)
    public List<LeaderboardResponse> getCurrentLeaderboard() {

        CompetitionEntity competition =
                competitionRepository
                        .findFirstByStatusOrderByWeekStartDesc(
                                CompetitionEntity.CompetitionStatus
                                        .LEADERBOARD_VISIBLE
                        )
                        .orElse(null);

        if (competition == null) {
            return List.of();
        }

        return leaderboardRepository
                .findByCompetitionOrderByRankAsc(competition)
                .stream()
                .map(entry -> new LeaderboardResponse(
                        entry.getRank(),
                        entry.getStudent().getId(),
                        entry.getStudent().getUsername(),
                        entry.getTotalPoints(),
                        competition.getWeekStart(),
                        competition.getWeekEnd()
                ))
                .toList();
    }

    @Transactional
    public void generateLeaderboardForCompetition(
            CompetitionEntity competition
    ) {

        leaderboardRepository.deleteByCompetition(competition);

        List<UserEntity> students =
                userRepository
                        .findByRoleAndActiveTrueOrderByCreatedAtAsc(
                                UserEntity.Role.STUDENT
                        );

        List<UserWeeklyScore> scores =
                students.stream()
                        .map(student ->
                                new UserWeeklyScore(
                                        student,
                                        calculateWeeklyPoints(
                                                competition,
                                                student
                                        )
                                )
                        )
                        .sorted((first, second) -> {

                            int pointsComparison =
                                    Integer.compare(
                                            second.points(),
                                            first.points()
                                    );

                            if (pointsComparison != 0) {
                                return pointsComparison;
                            }

                            return first.student()
                                    .getCreatedAt()
                                    .compareTo(
                                            second.student()
                                                    .getCreatedAt()
                                    );
                        })
                        .limit(3)
                        .toList();

        for (int i = 0; i < scores.size(); i++) {

            UserWeeklyScore score = scores.get(i);

            LeaderboardEntity entry =
                    new LeaderboardEntity();

            entry.setCompetition(competition);
            entry.setStudent(score.student());
            entry.setRank(i + 1);
            entry.setTotalPoints(score.points());

            leaderboardRepository.save(entry);
        }
    }

    private int calculateWeeklyPoints(
            CompetitionEntity competition,
            UserEntity student
    ) {
        int points = 0;

        List<QuestionEntity> questions =
                questionRepository
                        .findByCompetitionOrderByPostedAtAsc(
                                competition
                        );

        // 5 points for every question posted by this student
        for (QuestionEntity question : questions) {

            if (question.getPostedBy()
                    .getId()
                    .equals(student.getId())) {

                points += 5;
            }
        }

        // Points from completed attempts in this competition
        List<AttemptEntity> attempts =
                attemptRepository.findByQuestionCompetition(
                        competition
                );

        for (AttemptEntity attempt : attempts) {

            if (attempt.getStudent()
                    .getId()
                    .equals(student.getId())
                    && attempt.getStatus()
                    == AttemptEntity.AttemptStatus.COMPLETED) {

                points += attempt.getTotalPoints();
            }
        }

        return points;
    }

    private record UserWeeklyScore(
            UserEntity student,
            int points
    ) {
    }

    @Transactional(readOnly = true)
    public List<LeaderboardResponse> previewCurrentLeaderboard() {

        CompetitionEntity competition =
                competitionService.getOrCreateCurrentCompetition();

        List<UserWeeklyScore> scores =
                userRepository
                        .findByRoleAndActiveTrueOrderByCreatedAtAsc(
                                UserEntity.Role.STUDENT
                        )
                        .stream()
                        .map(student ->
                                new UserWeeklyScore(
                                        student,
                                        calculateWeeklyPoints(
                                                competition,
                                                student
                                        )
                                )
                        )
                        .sorted((first, second) -> {

                            int comparison =
                                    Integer.compare(
                                            second.points(),
                                            first.points()
                                    );

                            if (comparison != 0) {
                                return comparison;
                            }

                            return first.student()
                                    .getCreatedAt()
                                    .compareTo(
                                            second.student()
                                                    .getCreatedAt()
                                    );
                        })
                        .limit(3)
                        .toList();

        return java.util.stream.IntStream
                .range(0, scores.size())
                .mapToObj(index -> {

                    UserWeeklyScore score = scores.get(index);

                    return new LeaderboardResponse(
                            index + 1,
                            score.student().getId(),
                            score.student().getUsername(),
                            score.points(),
                            competition.getWeekStart(),
                            competition.getWeekEnd()
                    );
                })
                .toList();
    }

    private List<LeaderboardResponse> getLeaderboardForCompetition(
            CompetitionEntity competition
    ) {
        return leaderboardRepository
                .findByCompetitionOrderByRankAsc(competition)
                .stream()
                .map(entry -> new LeaderboardResponse(
                        entry.getRank(),
                        entry.getStudent().getId(),
                        entry.getStudent().getUsername(),
                        entry.getTotalPoints(),
                        competition.getWeekStart(),
                        competition.getWeekEnd()
                ))
                .toList();
    }

    public void broadcastLeaderboard(
            CompetitionEntity competition
    ) {
        webSocketBroadcastService.broadcastLeaderboard(
                getLeaderboardForCompetition(competition)
        );
    }
}