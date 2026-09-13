package com.p2plearn.learning.service;

import com.p2plearn.learning.dto.QuestionListResponse;
import com.p2plearn.learning.dto.QuestionRequest;
import com.p2plearn.learning.dto.QuestionResponse;
import com.p2plearn.learning.entity.AttemptEntity;
import com.p2plearn.learning.entity.CompetitionEntity;
import com.p2plearn.learning.entity.QuestionEntity;
import com.p2plearn.learning.entity.UserEntity;
import com.p2plearn.learning.repository.AttemptRepository;
import com.p2plearn.learning.repository.QuestionRepository;
import com.p2plearn.learning.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class QuestionService {

    private static final int QUESTION_POST_POINTS = 5;

    private static final ZoneId COMPETITION_ZONE =
            ZoneId.of("Asia/Kolkata");

    private final WebSocketBroadcastService webSocketBroadcastService;
    private final AttemptRepository attemptRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CompetitionService competitionService;

    public QuestionService(
            QuestionRepository questionRepository,
            UserRepository userRepository,
            CompetitionService competitionService,
            AttemptRepository attemptRepository,
            WebSocketBroadcastService webSocketBroadcastService
    ) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.competitionService = competitionService;
        this.attemptRepository = attemptRepository;
        this.webSocketBroadcastService = webSocketBroadcastService;
    }

    @Transactional
    public QuestionResponse postQuestion(
            QuestionRequest request,
            String username
    ) {

        UserEntity student = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found"
                        )
                );

        if (student.getRole() != UserEntity.Role.STUDENT) {
            throw new IllegalArgumentException(
                    "Only students can post questions"
            );
        }

        if (!Boolean.TRUE.equals(student.getActive())) {
            throw new IllegalArgumentException(
                    "Your account is not approved"
            );
        }

        CompetitionEntity competition =
                competitionService
                        .getOrCreateCurrentCompetition();

        /*
         * IMPORTANT:
         * Always use India time for competition dates.
         */
        LocalDate today =
                LocalDate.now(COMPETITION_ZONE);

        /*
         * One question per student per day.
         */
        if (questionRepository
                .existsByCompetitionAndPostedByAndPostedDate(
                        competition,
                        student,
                        today
                )) {

            throw new IllegalArgumentException(
                    "You have already posted a question today"
            );
        }

        String leetcodeUrl =
                request.getLeetcodeUrl().trim();

        if (!leetcodeUrl.startsWith(
                "https://leetcode.com/"
        )) {
            throw new IllegalArgumentException(
                    "Only valid LeetCode URLs are allowed"
            );
        }

        QuestionEntity question =
                new QuestionEntity();

        question.setCompetition(competition);
        question.setPostedBy(student);

        question.setTitle(
                request.getTitle().trim()
        );

        question.setDescription(
                request.getDescription() == null
                        ? null
                        : request.getDescription().trim()
        );

        question.setLeetcodeUrl(leetcodeUrl);

        /*
         * Store the posting date using Asia/Kolkata.
         */
        question.setPostedDate(today);

        QuestionEntity savedQuestion =
                questionRepository.save(question);

        /*
         * Give the student 5 points for posting.
         */
        student.setTotalPoints(
                student.getTotalPoints()
                        + QUESTION_POST_POINTS
        );

        userRepository.save(student);

        /*
         * Broadcast the newly posted question.
         */
        webSocketBroadcastService.broadcastQuestion(
                new QuestionResponse(
                        savedQuestion.getId(),
                        competition.getId(),
                        student.getId(),
                        student.getUsername(),
                        savedQuestion.getTitle(),
                        savedQuestion.getDescription(),
                        savedQuestion.getLeetcodeUrl(),
                        savedQuestion.getPostedDate(),
                        savedQuestion.getPostedAt(),
                        QUESTION_POST_POINTS
                )
        );

        return new QuestionResponse(
                savedQuestion.getId(),
                competition.getId(),
                student.getId(),
                student.getUsername(),
                savedQuestion.getTitle(),
                savedQuestion.getDescription(),
                savedQuestion.getLeetcodeUrl(),
                savedQuestion.getPostedDate(),
                savedQuestion.getPostedAt(),
                QUESTION_POST_POINTS
        );
    }

    @Transactional(readOnly = true)
    public List<QuestionListResponse> getCurrentCompetitionQuestions(
            String username
    ) {

        UserEntity student = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found"
                        )
                );

        if (student.getRole() != UserEntity.Role.STUDENT) {
            throw new IllegalArgumentException(
                    "Only students can view competition questions"
            );
        }

        if (!Boolean.TRUE.equals(student.getActive())) {
            throw new IllegalArgumentException(
                    "Your account is not approved"
            );
        }

        CompetitionEntity competition =
                competitionService
                        .getOrCreateCurrentCompetition();

        /*
         * Current date according to India time.
         */
        LocalDate today =
                LocalDate.now(COMPETITION_ZONE);

        /*
         * Only return questions posted TODAY.
         *
         * Old questions remain in the database for weekly
         * scoring/history, but they are no longer visible
         * in the current day's question list.
         */
        return questionRepository
                .findByCompetitionOrderByPostedAtAsc(competition)
                .stream()
                .filter(question ->
                        today.equals(
                                question.getPostedDate()
                        )
                )
                .map(question -> {

                    boolean ownQuestion =
                            question.getPostedBy()
                                    .getId()
                                    .equals(student.getId());

                    AttemptEntity attempt =
                            attemptRepository
                                    .findByQuestionAndStudent(
                                            question,
                                            student
                                    )
                                    .orElse(null);

                    boolean attempted =
                            attempt != null;

                    String attemptStatus =
                            attempted
                                    ? attempt.getStatus().name()
                                    : null;

                    Integer attemptPoints =
                            attempted
                                    ? attempt.getTotalPoints()
                                    : 0;

                    return new QuestionListResponse(
                            question.getId(),
                            question.getPostedBy().getId(),
                            question.getPostedBy().getUsername(),
                            question.getTitle(),
                            question.getDescription(),
                            question.getLeetcodeUrl(),
                            question.getPostedDate(),
                            question.getPostedAt(),
                            ownQuestion,
                            attempted,
                            attemptStatus,
                            attemptPoints
                    );
                })
                .toList();
    }
}