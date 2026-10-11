package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.dto.JwtAuthResponse;
import com.edtech.edtech_backend.dto.LoginDto;
import com.edtech.edtech_backend.dto.OtpVerificationRequestDto;
import com.edtech.edtech_backend.dto.SignUpDto;
import com.edtech.edtech_backend.dto.VerifyOtpDto;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@org.springframework.web.bind.annotation.CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> requestRegistration(@RequestBody SignUpDto signUpDto) {
        authService.requestRegistration(signUpDto);
        return ResponseEntity.ok("OTP sent to your email. Please verify to complete registration.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyAndRegister(@RequestBody OtpVerificationRequestDto verificationDto) {
        try {
            User newUser = authService.completeRegistration(verificationDto);
            return ResponseEntity.ok("User registered successfully! Welcome, " + newUser.getName());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
        JwtAuthResponse response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }

    // Endpoint to request password reset OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("Email is required.");
        }
        try {
            authService.requestPasswordReset(email);
            return ResponseEntity.ok("OTP for password reset sent to email.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint to verify OTP and set new password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody VerifyOtpDto verifyOtpDto) {
        try {
            authService.resetPassword(verifyOtpDto);
            return ResponseEntity.ok("Password reset successful. You can now login with your new password.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}