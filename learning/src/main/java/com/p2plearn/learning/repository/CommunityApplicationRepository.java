package com.p2plearn.learning.repository;

import com.p2plearn.learning.entity.CommunityApplicationEntity;
import com.p2plearn.learning.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityApplicationRepository
        extends JpaRepository<CommunityApplicationEntity, Long> {

    Optional<CommunityApplicationEntity> findByStudent(UserEntity student);

    Optional<CommunityApplicationEntity> findByStudentAndStatus(
            UserEntity student,
            CommunityApplicationEntity.ApplicationStatus status
    );

    List<CommunityApplicationEntity> findByStatusOrderByCreatedAtAsc(
            CommunityApplicationEntity.ApplicationStatus status
    );

    boolean existsByStudentAndStatus(
            UserEntity student,
            CommunityApplicationEntity.ApplicationStatus status
    );
}
