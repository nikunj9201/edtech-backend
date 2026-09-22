package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.StudentCreateRequestDto;
import com.edtech.edtech_backend.dto.StudentDto;
import com.edtech.edtech_backend.dto.StudentUpdateRequestDto;
import com.edtech.edtech_backend.security.service.UserDetailsImpl;
import com.edtech.edtech_backend.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @PostMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentDto> createStudent(@RequestBody StudentCreateRequestDto request) {
        return ResponseEntity.ok(studentService.createStudent(request));
    }

    @GetMapping("/students/me")
    public ResponseEntity<StudentDto> getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(studentService.getCurrentStudentProfile(userDetails.getId()));
    }

    @PutMapping("/students/me")
    public ResponseEntity<StudentDto> updateMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @RequestBody StudentUpdateRequestDto request) {
        return ResponseEntity.ok(studentService.updateCurrentStudentProfile(userDetails.getId(), request));
    }

    @GetMapping("/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getStudentById(studentId));
    }

    @PutMapping("/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Long studentId, @RequestBody StudentUpdateRequestDto request) {
        return ResponseEntity.ok(studentService.updateStudent(studentId, request));
    }

    @DeleteMapping("/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }
}
