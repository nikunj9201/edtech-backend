package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.dto.SubscriptionActivationRequest;
import com.edtech.edtech_backend.dto.SubscriptionPlanDto;
import com.edtech.edtech_backend.dto.UserSubscriptionDto;
import com.edtech.edtech_backend.entity.*;
import com.edtech.edtech_backend.model.PaymentStatus;
import com.edtech.edtech_backend.model.SubscriptionStatus;
import com.edtech.edtech_backend.repository.*;
import com.edtech.edtech_backend.service.SubscriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionPlanRepository planRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public SubscriptionServiceImpl(SubscriptionPlanRepository planRepository,
                                   UserSubscriptionRepository userSubscriptionRepository,
                                   PaymentRepository paymentRepository,
                                   UserRepository userRepository,
                                   CourseRepository courseRepository) {
        this.planRepository = planRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public List<SubscriptionPlan> getAllPlans() {
        return planRepository.findByActive(true);
    }

    @Override
    public SubscriptionPlan createPlan(SubscriptionPlanDto planDto) {
        // ... (implementation remains the same)
        return null;
    }

    @Override
    @Transactional
    public UserSubscriptionDto purchaseSubscription(User user, Long planId, String paymentMethod) {
        // ... (implementation remains the same)
        return null;
    }

    @Override
    public UserSubscriptionDto getMySubscription(User user) {
        // ... (implementation remains the same)
        return null;
    }

    @Override
    public List<UserSubscriptionDto> getSubscriptionHistory(User user) {
        // ... (implementation remains the same)
        return null;
    }

    @Override
    @Transactional
    public UserSubscription activateSubscription(SubscriptionActivationRequest request, User admin) {
        User student = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + request.getUserId()));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + request.getCourseId()));

        // Deactivate any existing subscription for this course
        userSubscriptionRepository.findByUserAndCourse(student, course)
                .ifPresent(sub -> {
                    sub.setStatus(SubscriptionStatus.CANCELLED);
                    userSubscriptionRepository.save(sub);
                });

        UserSubscription newSubscription = new UserSubscription();
        newSubscription.setUser(student);
        newSubscription.setCourse(course);
        newSubscription.setStartDate(LocalDate.now());
        newSubscription.setEndDate(LocalDate.now().plusDays(request.getDurationInDays()));
        newSubscription.setStatus(SubscriptionStatus.ACTIVE);
        newSubscription.setActivatedByAdmin(admin); // Link the admin who activated it

        return userSubscriptionRepository.save(newSubscription);
    }
    
    // ... other private helper methods
}
