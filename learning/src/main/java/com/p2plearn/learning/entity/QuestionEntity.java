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
        name = "questions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_one_question_per_day",
                        columnNames = {
                                "competition_id",
                                "posted_by",
                                "posted_date"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "competition_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_question_competition"
            )
    )
    private CompetitionEntity competition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "posted_by",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_question_user"
            )
    )
    private UserEntity postedBy;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "leetcode_url", nullable = false, length = 500)
    private String leetcodeUrl;

    @Column(name = "posted_at", nullable = false, updatable = false)
    private LocalDateTime postedAt;

    @Column(name = "posted_date", nullable = false, updatable = false)
    private LocalDate postedDate;

    @PrePersist
    protected void onCreate() {

        if (postedAt == null) {
            postedAt = LocalDateTime.now();
        }

        if (postedDate == null) {
            postedDate = LocalDate.now();
        }
    }
}