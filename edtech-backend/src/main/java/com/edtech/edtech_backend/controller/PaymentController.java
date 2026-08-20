package com.edtech.edtech_backend.controller;

import com.edtech.edtech_backend.entity.Course;
import com.edtech.edtech_backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${whatsapp.admin.number}")
    private String adminPhoneNumber;

    private final CourseRepository courseRepository;

    public PaymentController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping("/whatsapp-link/{courseId}")
    public ResponseEntity<?> getWhatsAppPurchaseLink(@PathVariable Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

        String courseName = course.getTitle();
        String message = String.format(
                "Hello Admin, I want to purchase the course \"%s\" (ID: %d). Here is my payment screenshot.",
                courseName, courseId
        );

        try {
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
            String whatsappUrl = String.format("https://wa.me/%s?text=%s", adminPhoneNumber, encodedMessage);
            return ResponseEntity.ok(Map.of("whatsappUrl", whatsappUrl));
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Could not encode WhatsApp message"));
        }
    }
}
