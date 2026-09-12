package com.p2plearn.learning.service;

import com.p2plearn.learning.entity.UserEntity;
import com.p2plearn.learning.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminStudentService {

    private final UserRepository userRepository;

    public AdminStudentService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getActiveStudents() {

        return userRepository
                .findByRoleAndActiveTrueOrderByCreatedAtAsc(
                        UserEntity.Role.STUDENT
                );
    }

    @Transactional
    public void removeStudent(Long studentId) {

        UserEntity student = userRepository
                .findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found"
                        )
                );

        if (student.getRole() != UserEntity.Role.STUDENT) {
            throw new IllegalArgumentException(
                    "Only students can be removed"
            );
        }

        userRepository.delete(student);
    }
}