package com.p2plearn.learning.repository;

import com.p2plearn.learning.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<UserEntity> findByRoleAndActiveTrueOrderByCreatedAtAsc(
            UserEntity.Role role
    );

    List<UserEntity> findTop3ByRoleAndActiveTrueOrderByTotalPointsDescCreatedAtAsc(
            UserEntity.Role role
    );
}
