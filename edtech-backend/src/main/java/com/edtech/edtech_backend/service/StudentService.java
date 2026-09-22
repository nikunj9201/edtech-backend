package com.edtech.edtech_backend.service;

import com.edtech.edtech_backend.dto.StudentCreateRequestDto;
import com.edtech.edtech_backend.dto.StudentDto;
import com.edtech.edtech_backend.dto.StudentUpdateRequestDto;

import java.util.List;

public interface StudentService {
    List<StudentDto> getAllStudents();
    StudentDto getStudentById(Long studentId);
    StudentDto createStudent(StudentCreateRequestDto request);
    StudentDto updateStudent(Long studentId, StudentUpdateRequestDto request);
    void deleteStudent(Long studentId);
    StudentDto getCurrentStudentProfile(Long userId);
    StudentDto updateCurrentStudentProfile(Long userId, StudentUpdateRequestDto request);
}
