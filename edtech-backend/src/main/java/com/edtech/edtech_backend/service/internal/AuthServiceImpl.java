package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.dto.JwtAuthResponse;
import com.edtech.edtech_backend.dto.LoginDto;
import com.edtech.edtech_backend.dto.OtpVerificationRequestDto;
import com.edtech.edtech_backend.dto.SignUpDto;
import com.edtech.edtech_backend.dto.VerifyOtpDto;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.exception.UserAlreadyExistsException;
import com.edtech.edtech_backend.model.Role;
import com.edtech.edtech_backend.repository.UserRepository;
import com.edtech.edtech_backend.security.jwt.JwtUtils;
import com.edtech.edtech_backend.security.service.UserDetailsImpl;
import com.edtech.edtech_backend.service.AuthService;
import com.edtech.edtech_backend.service.EmailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    // In-memory cache for pending registrations and password resets
    private final Map<String, SignUpDto> pendingUsers = new ConcurrentHashMap<>();
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
                           PasswordEncoder passwordEncoder, JwtUtils jwtUtils, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }

    @Override
    public JwtAuthResponse login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtAuthResponse(jwt, userDetails.getId(), userDetails.getEmail(), roles);
    }

    @Override
    public void requestRegistration(SignUpDto signUpDto) {
        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email is already registered!");
        }

        String otp = generateOtp();
        otpStorage.put(signUpDto.getEmail(), otp);
        pendingUsers.put(signUpDto.getEmail(), signUpDto);

        String subject = "Your OTP for EdTech Platform Registration";
        String body = "Your OTP is: " + otp;

        emailService.sendEmail(signUpDto.getEmail(), subject, body);
    }

    @Override
    public User completeRegistration(OtpVerificationRequestDto verificationDto) {
        String storedOtp = otpStorage.get(verificationDto.getEmail());
        SignUpDto pendingUserDto = pendingUsers.get(verificationDto.getEmail());

        if (pendingUserDto == null || storedOtp == null || !storedOtp.equals(verificationDto.getOtp())) {
            throw new RuntimeException("Invalid OTP or registration request.");
        }

        User user = new User();
        user.setName(pendingUserDto.getName());
        user.setEmail(pendingUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(pendingUserDto.getPassword()));
        user.setContactNumber(pendingUserDto.getContactNumber());
        user.setCountry(pendingUserDto.getCountry());
        user.setCity(pendingUserDto.getCity());
        user.setDateOfBirth(pendingUserDto.getDateOfBirth());
        user.setRole(Role.STUDENT);

        User savedUser = userRepository.save(user);

        // Clean up the cache
        otpStorage.remove(verificationDto.getEmail());
        pendingUsers.remove(verificationDto.getEmail());

        return savedUser;
    }

    // --- Forgot-password methods ---
    @Override
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not registered."));
        String otp = generateOtp();
        otpStorage.put(email, otp);

        String subject = "Your OTP to reset password";
        String body = "Your OTP to reset your EdTech account password is: " + otp + "\nThis OTP is valid for a short time.";

        emailService.sendEmail(email, subject, body);
    }

    @Override
    public void resetPassword(VerifyOtpDto verifyOtpDto) {
        String storedOtp = otpStorage.get(verifyOtpDto.getEmail());
        if (storedOtp == null || !storedOtp.equals(verifyOtpDto.getOtp())) {
            throw new RuntimeException("Invalid OTP or OTP expired.");
        }

        User user = userRepository.findByEmail(verifyOtpDto.getEmail()).orElseThrow(() -> new RuntimeException("User not found."));
        if (verifyOtpDto.getPassword() == null || verifyOtpDto.getPassword().isBlank()) {
            throw new RuntimeException("Password must not be empty.");
        }

        user.setPassword(passwordEncoder.encode(verifyOtpDto.getPassword()));
        userRepository.save(user);

        // remove OTP after successful reset
        otpStorage.remove(verifyOtpDto.getEmail());
    }

    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}