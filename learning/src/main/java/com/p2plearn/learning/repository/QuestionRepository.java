package com.p2plearn.learning.repository;

import com.p2plearn.learning.entity.CompetitionEntity;
import com.p2plearn.learning.entity.QuestionEntity;
import com.p2plearn.learning.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    List<QuestionEntity> findByCompetitionOrderByPostedAtAsc(
            CompetitionEntity competition
    );

    Optional<QuestionEntity> findByCompetitionAndPostedBy(
            CompetitionEntity competition,
            UserEntity postedBy
    );

    boolean existsByCompetitionAndPostedBy(
            CompetitionEntity competition,
            UserEntity postedBy
    );

    boolean existsByCompetitionAndPostedByAndPostedDate(CompetitionEntity competition, UserEntity student, LocalDate today);
}