package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.LoginDto;
import com.edtech.edtech_backend.dto.SignUpDto;
import com.edtech.edtech_backend.dto.VerifyOtpDto;
import com.edtech.edtech_backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody SignUpDto signUpDto) {
        authService.generateAndSendOtp(signUpDto.getEmail());
        return ResponseEntity.ok("OTP sent to your email. Please verify to complete registration.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpDto verifyOtpDto) {
        if (authService.verifyOtp(verifyOtpDto)) {
            SignUpDto signUpDto = new SignUpDto();
            signUpDto.setEmail(verifyOtpDto.getEmail());
            signUpDto.setPassword(verifyOtpDto.getPassword());
            signUpDto.setContactNumber(verifyOtpDto.getContactNumber());
            signUpDto.setCountry(verifyOtpDto.getCountry());
            authService.register(signUpDto);
            return ResponseEntity.ok("User registered successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);
        return ResponseEntity.ok(token);
    }
}