package com.p2plearn.learning.repository;

import com.p2plearn.learning.entity.CompetitionEntity;
import com.p2plearn.learning.entity.LeaderboardEntity;
import com.p2plearn.learning.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaderboardRepository
        extends JpaRepository<LeaderboardEntity, Long> {

    List<LeaderboardEntity> findByCompetitionOrderByRankAsc(
            CompetitionEntity competition
    );

    Optional<LeaderboardEntity> findByCompetitionAndStudent(
            CompetitionEntity competition,
            UserEntity student
    );

    boolean existsByCompetitionAndStudent(
            CompetitionEntity competition,
            UserEntity student
    );

    void deleteByCompetition(
            CompetitionEntity competition
    );
}