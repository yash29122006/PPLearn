package com.p2plearn.learning.service;

import com.p2plearn.learning.dto.AttemptResponse;
import com.p2plearn.learning.dto.QuestionAttemptResponse;
import com.p2plearn.learning.entity.AttemptEntity;
import com.p2plearn.learning.entity.QuestionEntity;
import com.p2plearn.learning.entity.UserEntity;
import com.p2plearn.learning.repository.AttemptRepository;
import com.p2plearn.learning.repository.QuestionRepository;
import com.p2plearn.learning.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AttemptService {

    private final WebSocketBroadcastService webSocketBroadcastService;
    private final AttemptRepository attemptRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public AttemptService(
            AttemptRepository attemptRepository,
            QuestionRepository questionRepository,
            UserRepository userRepository,
            WebSocketBroadcastService webSocketBroadcastService
    ) {
        this.attemptRepository = attemptRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.webSocketBroadcastService = webSocketBroadcastService;
    }

    @Transactional
    public AttemptResponse startAttempt(
            Long questionId,
            String username
    ) {

        UserEntity student = findStudent(username);

        QuestionEntity question = questionRepository
                .findById(questionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Question not found"
                        )
                );

        if (question.getPostedBy().getId()
                .equals(student.getId())) {

            throw new IllegalArgumentException(
                    "You cannot attempt your own question"
            );
        }

        if (attemptRepository
                .existsByQuestionAndStudent(
                        question,
                        student
                )) {

            throw new IllegalArgumentException(
                    "You have already attempted this question"
            );
        }

        AttemptEntity attempt = new AttemptEntity();

        attempt.setQuestion(question);
        attempt.setStudent(student);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setStatus(
                AttemptEntity.AttemptStatus.STARTED
        );
        attempt.setPointsEarned(0);
        attempt.setBonusPoints(0);
        attempt.setTotalPoints(0);

        AttemptEntity savedAttempt =
                attemptRepository.save(attempt);
        webSocketBroadcastService.broadcastAttempt(
                toResponse(savedAttempt)
        );

        return toResponse(savedAttempt);
    }

    @Transactional
    public AttemptResponse finishAttempt(
            Long questionId,
            String username
    ) {

        UserEntity student = findStudent(username);

        QuestionEntity question = questionRepository
                .findById(questionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Question not found"
                        )
                );

        AttemptEntity attempt =
                attemptRepository
                        .findByQuestionAndStudent(
                                question,
                                student
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "You have not started this question"
                                )
                        );

        if (attempt.getStatus()
                == AttemptEntity.AttemptStatus.COMPLETED) {

            throw new IllegalArgumentException(
                    "This attempt is already completed"
            );
        }

        LocalDateTime finishedAt =
                LocalDateTime.now();

        attempt.setFinishedAt(finishedAt);

        /*
         * Scoring:
         *
         * Within 15 minutes of question posting:
         * 15 base + 5 bonus = 20
         *
         * After 15 minutes:
         * 10 points
         */
        Duration timeSincePosting =
                Duration.between(
                        question.getPostedAt(),
                        finishedAt
                );

        int pointsEarned;
        int bonusPoints;

        Duration fifteenMinutes = Duration.ofMinutes(15);

        if (!timeSincePosting.isNegative()
                && timeSincePosting.compareTo(fifteenMinutes) <= 0) {

            pointsEarned = 15;
            bonusPoints = 5;

        } else {

            pointsEarned = 10;
            bonusPoints = 0;
        }

        int totalPoints =
                pointsEarned + bonusPoints;

        attempt.setPointsEarned(pointsEarned);
        attempt.setBonusPoints(bonusPoints);
        attempt.setTotalPoints(totalPoints);

        attempt.setStatus(
                AttemptEntity.AttemptStatus.COMPLETED
        );

        attemptRepository.save(attempt);

        student.setTotalPoints(
                student.getTotalPoints() + totalPoints
        );

        userRepository.save(student);
        webSocketBroadcastService.broadcastAttempt(
                toResponse(attempt)
        );

        return toResponse(attempt);
    }

    private UserEntity findStudent(String username) {

        UserEntity student = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found"
                        )
                );

        if (student.getRole() != UserEntity.Role.STUDENT) {
            throw new IllegalArgumentException(
                    "Only students can attempt questions"
            );
        }

        if (!Boolean.TRUE.equals(student.getActive())) {
            throw new IllegalArgumentException(
                    "Your account is not approved"
            );
        }

        return student;
    }

    private AttemptResponse toResponse(
            AttemptEntity attempt
    ) {

        return new AttemptResponse(
                attempt.getId(),
                attempt.getQuestion().getId(),
                attempt.getStudent().getId(),
                attempt.getStudent().getUsername(),
                attempt.getStartedAt(),
                attempt.getFinishedAt(),
                attempt.getStatus().name(),
                attempt.getPointsEarned(),
                attempt.getBonusPoints(),
                attempt.getTotalPoints()
        );
    }
    @Transactional(readOnly = true)
    public List<QuestionAttemptResponse> getQuestionAttempts(
            Long questionId,
            String username
    ) {
        UserEntity student = findStudent(username);

        QuestionEntity question = questionRepository
                .findById(questionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Question not found"
                        )
                );

        return attemptRepository
                .findByQuestionOrderByFinishedAtAsc(question)
                .stream()
                .map(attempt -> new QuestionAttemptResponse(
                        attempt.getId(),
                        attempt.getStudent().getId(),
                        attempt.getStudent().getUsername(),
                        attempt.getStartedAt(),
                        attempt.getFinishedAt(),
                        attempt.getStatus().name(),
                        attempt.getPointsEarned(),
                        attempt.getBonusPoints(),
                        attempt.getTotalPoints()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public AttemptResponse getMyAttempt(
            Long questionId,
            String username
    ) {
        UserEntity student = findStudent(username);

        QuestionEntity question = questionRepository
                .findById(questionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Question not found"
                        )
                );

        AttemptEntity attempt =
                attemptRepository.findByQuestionAndStudent(
                                question,
                                student
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "No attempt found for this question"
                                )
                        );

        return toResponse(attempt);
    }
}