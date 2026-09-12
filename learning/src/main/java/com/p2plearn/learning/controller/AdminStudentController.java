package com.p2plearn.learning.controller;

import com.p2plearn.learning.dto.AdminStudentResponse;
import com.p2plearn.learning.entity.UserEntity;
import com.p2plearn.learning.service.AdminStudentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/students")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStudentController {

    private final AdminStudentService adminStudentService;

    public AdminStudentController(
            AdminStudentService adminStudentService
    ) {
        this.adminStudentService = adminStudentService;
    }

    @GetMapping
    public ResponseEntity<?> getActiveStudents() {

        List<UserEntity> students =
                adminStudentService.getActiveStudents();

        List<AdminStudentResponse> response =
                students.stream()
                        .map(student -> new AdminStudentResponse(
                                student.getId(),
                                student.getUsername(),
                                student.getEmail(),
                                student.getTotalPoints(),
                                student.getCreatedAt()
                        ))
                        .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<?> removeStudent(
            @PathVariable Long studentId
    ) {
        try {
            adminStudentService.removeStudent(studentId);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "Student removed successfully"
                    )
            );

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    exception.getMessage()
                            )
                    );
        }
    }
}