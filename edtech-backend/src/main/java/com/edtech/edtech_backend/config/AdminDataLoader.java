package com.edtech.edtech_backend.config;

import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.model.Role;
import com.edtech.edtech_backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if an admin user already exists
        if (userRepository.findByEmail("anikunjpatidar8888@gmail.com").isEmpty()) {
            User adminUser = new User();
            adminUser.setEmail("nikunjpatidar8888@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("Nikunj8888@")); // Set a default password
            adminUser.setRole(Role.ADMIN);
            adminUser.setCountry("IN"); // Set a default country

            userRepository.save(adminUser);
            System.out.println("Admin user created successfully!");
        } else {
            System.out.println("Admin user already exists.");
        }
    }
}