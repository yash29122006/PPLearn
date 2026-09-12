package com.p2plearn.learning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "attempts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_student_question",
                        columnNames = {
                                "question_id",
                                "student_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttemptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==========================================
    // QUESTION
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "question_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_attempt_question"
            )
    )
    private QuestionEntity question;

    // ==========================================
    // STUDENT
    // ==========================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_attempt_student"
            )
    )
    private UserEntity student;

    // ==========================================
    // ATTEMPT TIME
    // ==========================================

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    // ==========================================
    // STATUS
    // ==========================================

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private AttemptStatus status = AttemptStatus.STARTED;

    // ==========================================
    // POINTS
    // ==========================================

    @Column(
            name = "points_earned",
            nullable = false
    )
    private Integer pointsEarned = 0;

    @Column(
            name = "bonus_points",
            nullable = false
    )
    private Integer bonusPoints = 0;

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

    // ==========================================
    // ENUM
    // ==========================================

    public enum AttemptStatus {
        STARTED,
        COMPLETED
    }
}