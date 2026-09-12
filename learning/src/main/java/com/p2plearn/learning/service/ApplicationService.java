package com.p2plearn.learning.service;

import com.p2plearn.learning.dto.ApplicationDecisionRequest;
import com.p2plearn.learning.dto.ApplicationResponse;
import com.p2plearn.learning.entity.CommunityApplicationEntity;
import com.p2plearn.learning.entity.UserEntity;
import com.p2plearn.learning.repository.CommunityApplicationRepository;
import com.p2plearn.learning.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    private final CommunityApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationService(
            CommunityApplicationRepository applicationRepository,
            UserRepository userRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('STUDENT')")
    public ApplicationResponse getMyApplication(String studentUsername) {

        UserEntity student = userRepository
                .findByUsername(studentUsername)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found"
                        )
                );

        return applicationRepository
                .findByStudent(student)
                .map(this::toResponse)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No application found"
                        )
                );
    }

    @Transactional
    @PreAuthorize("hasRole('STUDENT')")
    public ApplicationResponse applyToCommunity(String studentUsername) {

        UserEntity student = userRepository
                .findByUsername(studentUsername)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found"
                        )
                );

        if (student.getRole() != UserEntity.Role.STUDENT) {
            throw new IllegalArgumentException(
                    "Only students can apply to the community"
            );
        }

        if (applicationRepository.existsByStudentAndStatus(
                student,
                CommunityApplicationEntity.ApplicationStatus.PENDING
        )) {
            throw new IllegalArgumentException(
                    "You already have a pending application"
            );
        }

        CommunityApplicationEntity application =
                applicationRepository.findByStudent(student)
                        .orElse(null);

        if (application != null) {

            if (application.getStatus()
                    == CommunityApplicationEntity.ApplicationStatus.ACCEPTED) {

                throw new IllegalArgumentException(
                        "You are already a community member"
                );
            }

            application.setStatus(
                    CommunityApplicationEntity.ApplicationStatus.PENDING
            );
            application.setReviewedBy(null);
            application.setReviewedAt(null);

            return toResponse(
                    applicationRepository.save(application)
            );
        }

        application = new CommunityApplicationEntity();
        application.setStudent(student);
        application.setStatus(
                CommunityApplicationEntity.ApplicationStatus.PENDING
        );

        return toResponse(
                applicationRepository.save(application)
        );
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<ApplicationResponse> getPendingApplications() {

        return applicationRepository
                .findByStatusOrderByCreatedAtAsc(
                        CommunityApplicationEntity.ApplicationStatus.PENDING
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ApplicationResponse decideApplication(
            Long applicationId,
            ApplicationDecisionRequest request,
            String adminUsername
    ) {

        CommunityApplicationEntity application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Application not found"
                                )
                        );

        if (application.getStatus()
                != CommunityApplicationEntity.ApplicationStatus.PENDING) {

            throw new IllegalArgumentException(
                    "This application has already been reviewed"
            );
        }

        UserEntity admin = userRepository
                .findByUsername(adminUsername)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Admin user not found"
                        )
                );

        if (admin.getRole() != UserEntity.Role.ADMIN) {
            throw new IllegalArgumentException(
                    "Only an admin can review applications"
            );
        }

        UserEntity student = application.getStudent();

        if (request.getDecision()
                == ApplicationDecisionRequest.Decision.ACCEPT) {

            application.setStatus(
                    CommunityApplicationEntity.ApplicationStatus.ACCEPTED
            );

            student.setActive(true);

        } else {

            application.setStatus(
                    CommunityApplicationEntity.ApplicationStatus.REJECTED
            );

            student.setActive(false);
        }

        application.setReviewedBy(admin);
        application.setReviewedAt(LocalDateTime.now());

        userRepository.save(student);
        applicationRepository.save(application);

        return toResponse(application);
    }

    private ApplicationResponse toResponse(
            CommunityApplicationEntity application
    ) {

        return new ApplicationResponse(
                application.getId(),
                application.getStudent().getId(),
                application.getStudent().getUsername(),
                application.getStudent().getEmail(),
                application.getStatus().name(),
                application.getCreatedAt()
        );
    }
}