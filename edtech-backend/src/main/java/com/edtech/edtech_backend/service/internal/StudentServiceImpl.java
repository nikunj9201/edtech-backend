package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.dto.StudentCreateRequestDto;
import com.edtech.edtech_backend.dto.StudentDto;
import com.edtech.edtech_backend.dto.StudentUpdateRequestDto;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.model.Role;
import com.edtech.edtech_backend.repository.UserRepository;
import com.edtech.edtech_backend.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<StudentDto> getAllStudents() {
        return userRepository.findAll().stream()
                .filter(user -> Role.STUDENT.equals(user.getRole()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDto getStudentById(Long studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        if (!Role.STUDENT.equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a student");
        }
        return toDto(user);
    }

    @Override
    @Transactional
    public StudentDto createStudent(StudentCreateRequestDto request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student payload is required");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setContactNumber(request.getContactNumber());
        user.setCountry(request.getCountry());
        user.setCity(request.getCity());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setRole(Role.STUDENT);

        return toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public StudentDto updateStudent(Long studentId, StudentUpdateRequestDto request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student payload is required");
        }

        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        if (!Role.STUDENT.equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a student");
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!request.getEmail().equalsIgnoreCase(user.getEmail()) && userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
            }
            user.setEmail(request.getEmail().trim());
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName().trim());
        }
        if (request.getContactNumber() != null) {
            user.setContactNumber(request.getContactNumber());
        }
        if (request.getCountry() != null) {
            user.setCountry(request.getCountry());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteStudent(Long studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        if (!Role.STUDENT.equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a student");
        }
        userRepository.delete(user);
    }

    @Override
    public StudentDto getCurrentStudentProfile(Long userId) {
        return getStudentById(userId);
    }

    @Override
    public StudentDto updateCurrentStudentProfile(Long userId, StudentUpdateRequestDto request) {
        return updateStudent(userId, request);
    }

    private StudentDto toDto(User user) {
        StudentDto dto = new StudentDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setContactNumber(user.getContactNumber());
        dto.setCountry(user.getCountry());
        dto.setCity(user.getCity());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        return dto;
    }
}
