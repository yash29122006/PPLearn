package com.p2plearn.learning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "leaderboard",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_leaderboard_student",
                        columnNames = {
                                "competition_id",
                                "student_id"
                        }
                ),
                @UniqueConstraint(
                        name = "uq_leaderboard_rank",
                        columnNames = {
                                "competition_id",
                                "rank"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==========================================
    // COMPETITION
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "competition_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_leaderboard_competition"
            )
    )
    private CompetitionEntity competition;

    // ==========================================
    // STUDENT
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_leaderboard_student"
            )
    )
    private UserEntity student;

    // ==========================================
    // RANK
    // ==========================================

    @Column(
            name = "rank",
            nullable = false
    )
    private Integer rank;

    // ==========================================
    // TOTAL POINTS
    // ==========================================

    @Column(
            name = "total_points",
            nullable = false
    )
    private Integer totalPoints = 0;

    // ==========================================
    // CREATED TIME
    // ==========================================

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
}