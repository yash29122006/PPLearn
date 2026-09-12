package com.p2plearn.learning.repository;

import com.p2plearn.learning.entity.CompetitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<CompetitionEntity, Long> {

    Optional<CompetitionEntity> findByStatus(
            CompetitionEntity.CompetitionStatus status
    );

    Optional<CompetitionEntity> findByWeekStart(LocalDate weekStart);

    boolean existsByWeekStart(LocalDate weekStart);

    Optional<CompetitionEntity>
    findFirstByStatusOrderByWeekStartDesc(
            CompetitionEntity.CompetitionStatus status
    );

    Optional<CompetitionEntity>
    findFirstByStatusAndWeekEndBeforeOrderByWeekStartDesc(
            CompetitionEntity.CompetitionStatus status,
            LocalDate date
    );
}