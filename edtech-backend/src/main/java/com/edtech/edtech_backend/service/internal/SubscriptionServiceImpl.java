package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.dto.CourseAccessStatusDto;
import com.edtech.edtech_backend.dto.StudentCourseAccessDto;
import com.edtech.edtech_backend.dto.SubscriptionActivationRequest;
import com.edtech.edtech_backend.dto.SubscriptionPlanDto;
import com.edtech.edtech_backend.dto.UserSubscriptionDto;
import com.edtech.edtech_backend.entity.*;
import com.edtech.edtech_backend.model.ContentAccessType;
import com.edtech.edtech_backend.model.PaymentStatus;
import com.edtech.edtech_backend.model.SubscriptionStatus;
import com.edtech.edtech_backend.repository.*;
import com.edtech.edtech_backend.service.SubscriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
        List<UserSubscription> subscriptions = userSubscriptionRepository.findByUserOrderByEndDateDesc(user);
        UserSubscriptionDto dto = new UserSubscriptionDto();
        dto.setSubscribed(false);

        if (subscriptions == null || subscriptions.isEmpty()) {
            return dto;
        }

        UserSubscription active = subscriptions.stream()
                .filter(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE)
                .filter(sub -> !sub.getEndDate().isBefore(LocalDate.now()))
                .findFirst()
                .orElse(null);

        if (active == null) {
            UserSubscription latest = subscriptions.get(0);
            dto.setSubscribed(false);
            dto.setCourseId(latest.getCourse() != null ? latest.getCourse().getId() : null);
            dto.setPlanName(latest.getPlan() != null ? latest.getPlan().getName() : "N/A");
            dto.setStartDate(latest.getStartDate());
            dto.setEndDate(latest.getEndDate());
            dto.setStatus(latest.getStatus());
            dto.setDaysRemaining(0);
            return dto;
        }

        dto.setSubscribed(true);
        dto.setCourseId(active.getCourse() != null ? active.getCourse().getId() : null);
        dto.setPlanName(active.getPlan() != null ? active.getPlan().getName() : "N/A");
        dto.setStartDate(active.getStartDate());
        dto.setEndDate(active.getEndDate());
        dto.setStatus(active.getStatus());
        dto.setDaysRemaining(Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), active.getEndDate())));
        return dto;
    }

    @Override
    public List<UserSubscriptionDto> getSubscriptionHistory(User user) {
        List<UserSubscription> subscriptions = userSubscriptionRepository.findByUserOrderByEndDateDesc(user);
        if (subscriptions == null || subscriptions.isEmpty()) {
            return List.of();
        }

        return subscriptions.stream().map(sub -> {
            UserSubscriptionDto dto = new UserSubscriptionDto();
            dto.setCourseId(sub.getCourse() != null ? sub.getCourse().getId() : null);
            dto.setSubscribed(sub.getStatus() == SubscriptionStatus.ACTIVE && !sub.getEndDate().isBefore(LocalDate.now()));
            dto.setPlanName(sub.getPlan() != null ? sub.getPlan().getName() : "N/A");
            dto.setStartDate(sub.getStartDate());
            dto.setEndDate(sub.getEndDate());
            dto.setStatus(sub.getStatus());
            dto.setDaysRemaining(Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), sub.getEndDate())));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<StudentCourseAccessDto> getMyCourses(User user) {
        List<Course> allCourses = courseRepository.findAll();
        return allCourses.stream()
                .filter(course -> hasAccessToCourse(user, course))
                .map(course -> {
                    StudentCourseAccessDto dto = new StudentCourseAccessDto();
                    dto.setCourseId(course.getId());
                    dto.setCourseTitle(course.getTitle());
                    dto.setThumbnailUrl(course.getThumbnailUrl());
                    dto.setOriginalPrice(course.getOriginalPrice());
                    dto.setDiscountedPrice(course.getDiscountedPrice());
                    dto.setAccessType(course.getAccessType());
                    dto.setAccessGranted(true);

                    Optional<UserSubscription> subscription = userSubscriptionRepository.findByUserAndCourse(user, course);
                    if (subscription.isPresent()) {
                        UserSubscription access = subscription.get();
                        dto.setStartDate(access.getStartDate());
                        dto.setEndDate(access.getEndDate());
                        dto.setStatus(access.getStatus().name());
                        dto.setDaysRemaining(access.getStatus() == SubscriptionStatus.ACTIVE
                                ? Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), access.getEndDate()))
                                : 0);
                    } else {
                        dto.setStartDate(null);
                        dto.setEndDate(null);
                        dto.setStatus("FREE");
                        dto.setDaysRemaining(0);
                    }
                    return dto;
                })
                .sorted(Comparator.comparing(StudentCourseAccessDto::getCourseId))
                .collect(Collectors.toList());
    }

    @Override
    public CourseAccessStatusDto getCourseAccess(User user, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

        CourseAccessStatusDto dto = new CourseAccessStatusDto();
        dto.setCourseId(course.getId());

        if (course.getAccessType() == ContentAccessType.FREE) {
            dto.setAccessGranted(true);
            dto.setStatus("FREE");
            dto.setStartDate(null);
            dto.setEndDate(null);
            dto.setDaysRemaining(0);
            return dto;
        }

        Optional<UserSubscription> subscription = userSubscriptionRepository.findByUserAndCourse(user, course);
        if (subscription.isEmpty()) {
            dto.setAccessGranted(false);
            dto.setStatus("NOT_SUBSCRIBED");
            dto.setStartDate(null);
            dto.setEndDate(null);
            dto.setDaysRemaining(0);
            return dto;
        }

        UserSubscription access = subscription.get();
        if (access.getStatus() == SubscriptionStatus.ACTIVE && !access.getEndDate().isBefore(LocalDate.now())) {
            dto.setAccessGranted(true);
            dto.setStatus(access.getStatus().name());
            dto.setStartDate(access.getStartDate());
            dto.setEndDate(access.getEndDate());
            dto.setDaysRemaining(Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), access.getEndDate())));
            return dto;
        }

        dto.setAccessGranted(false);
        dto.setStatus(access.getStatus() == SubscriptionStatus.EXPIRED ? "EXPIRED" : (access.getStatus() == null ? "NOT_SUBSCRIBED" : access.getStatus().name()));
        dto.setStartDate(access.getStartDate());
        dto.setEndDate(access.getEndDate());
        dto.setDaysRemaining(0);
        return dto;
    }

    public boolean hasAccessToCourse(User user, Course course) {
        if (user == null || course == null) {
            return false;
        }

        if (course.getAccessType() == ContentAccessType.FREE) {
            return true;
        }

        Optional<UserSubscription> subscription = userSubscriptionRepository.findByUserAndCourse(user, course);
        if (subscription.isEmpty()) {
            return false;
        }

        UserSubscription access = subscription.get();
        return access.getStatus() == SubscriptionStatus.ACTIVE && !access.getEndDate().isBefore(LocalDate.now());
    }

    @Override
    @Transactional
    public UserSubscription activateSubscription(SubscriptionActivationRequest request, User admin) {
        User student = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + request.getUserId()));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + request.getCourseId()));

        if (request.getDurationInDays() <= 0) {
            throw new RuntimeException("durationInDays must be greater than zero");
        }

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
        newSubscription.setActivatedByAdmin(admin);

        if (request.getPlanId() != null) {
            SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new RuntimeException("Subscription plan not found with ID: " + request.getPlanId()));
            newSubscription.setPlan(plan);
        }

        return userSubscriptionRepository.save(newSubscription);
    }

    @Override
    public List<UserSubscription> getAllSubscriptions() {
        return userSubscriptionRepository.findAll();
    }

    @Override
    public List<UserSubscription> getSubscriptionsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return userSubscriptionRepository.findByUserOrderByEndDateDesc(user);
    }

    @Override
    @Transactional
    public void deleteSubscription(Long subscriptionId) {
        UserSubscription subscription = userSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found with ID: " + subscriptionId));
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        userSubscriptionRepository.delete(subscription);
    }
    
    // ... other private helper methods
}
