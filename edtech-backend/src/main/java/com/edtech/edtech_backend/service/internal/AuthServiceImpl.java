package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.dto.LoginDto;
import com.edtech.edtech_backend.dto.SignUpDto;
import com.edtech.edtech_backend.dto.VerifyOtpDto;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.exception.UserAlreadyExistsException;
import com.edtech.edtech_backend.model.Role;
import com.edtech.edtech_backend.repository.UserRepository;
import com.edtech.edtech_backend.security.jwt.JwtUtils;
import com.edtech.edtech_backend.service.AuthService;
import com.edtech.edtech_backend.service.EmailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    // In-memory OTP storage (for demonstration purposes)
    private final Map<String, String> otpStorage = new HashMap<>();

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
                           PasswordEncoder passwordEncoder, JwtUtils jwtUtils, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }

    @Override
    public String login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }

    @Override
    public User register(SignUpDto signUpDto) {
        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email is already registered!");
        }

        User user = new User();
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setContactNumber(signUpDto.getContactNumber());
        user.setCountry(signUpDto.getCountry());
        user.setRole(Role.STUDENT);

        return userRepository.save(user);
    }

    @Override
    public void generateAndSendOtp(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email is already registered!");
        }

        String otp = generateOtp();
        otpStorage.put(email, otp);

        String subject = "Your OTP for EdTech Platform Registration";
        String body = "Your OTP is: " + otp;

        emailService.sendEmail(email, subject, body);
    }

    @Override
    public boolean verifyOtp(VerifyOtpDto verifyOtpDto) {
        String storedOtp = otpStorage.get(verifyOtpDto.getEmail());
        if (storedOtp != null && storedOtp.equals(verifyOtpDto.getOtp())) {
            otpStorage.remove(verifyOtpDto.getEmail()); // OTP is single-use
            return true;
        }
        return false;
    }

    private String generateOtp() {
        // Generate a 6-digit OTP
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}