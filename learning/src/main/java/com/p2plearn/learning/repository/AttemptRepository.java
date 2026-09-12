package com.p2plearn.learning.repository;

import com.p2plearn.learning.entity.AttemptEntity;
import com.p2plearn.learning.entity.QuestionEntity;
import com.p2plearn.learning.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.p2plearn.learning.entity.CompetitionEntity;
import java.util.List;
import java.util.Optional;

public interface AttemptRepository extends JpaRepository<AttemptEntity, Long> {

    Optional<AttemptEntity> findByQuestionAndStudent(
            QuestionEntity question,
            UserEntity student
    );

    boolean existsByQuestionAndStudent(
            QuestionEntity question,
            UserEntity student
    );

    List<AttemptEntity> findByQuestionOrderByFinishedAtAsc(
            QuestionEntity question
    );

    List<AttemptEntity> findByStudent(
            UserEntity student
    );

    List<AttemptEntity> findByQuestion(
            QuestionEntity question
    );

    List<AttemptEntity> findByQuestionCompetition(
            CompetitionEntity competition
    );
}