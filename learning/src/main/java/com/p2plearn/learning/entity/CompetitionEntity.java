package com.p2plearn.learning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "competitions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_competition_week",
                        columnNames = "week_start"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompetitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "week_start",
            nullable = false
    )
    private LocalDate weekStart;

    @Column(
            name = "week_end",
            nullable = false
    )
    private LocalDate weekEnd;

    @Column(
            name = "leaderboard_visible_until"
    )
    private LocalDateTime leaderboardVisibleUntil;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private CompetitionStatus status = CompetitionStatus.ACTIVE;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public enum CompetitionStatus {
        ACTIVE,
        COMPLETED,
        LEADERBOARD_VISIBLE,
        DELETED
    }
}