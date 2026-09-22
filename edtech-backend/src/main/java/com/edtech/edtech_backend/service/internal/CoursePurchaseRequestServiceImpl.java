package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.dto.CoursePurchaseRequestDto;
import com.edtech.edtech_backend.dto.PurchaseRequestResponseDto;
import com.edtech.edtech_backend.entity.Course;
import com.edtech.edtech_backend.entity.CoursePurchaseRequest;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.model.PurchaseRequestStatus;
import com.edtech.edtech_backend.repository.CoursePurchaseRequestRepository;
import com.edtech.edtech_backend.repository.CourseRepository;
import com.edtech.edtech_backend.repository.UserRepository;
import com.edtech.edtech_backend.repository.UserSubscriptionRepository;
import com.edtech.edtech_backend.service.CoursePurchaseRequestService;
import com.edtech.edtech_backend.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoursePurchaseRequestServiceImpl implements CoursePurchaseRequestService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CoursePurchaseRequestRepository requestRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriptionService subscriptionService;

    @Value("${whatsapp.admin.number}")
    private String whatsappAdminNumber;

    public CoursePurchaseRequestServiceImpl(CourseRepository courseRepository,
                                            UserRepository userRepository,
                                            CoursePurchaseRequestRepository requestRepository,
                                            UserSubscriptionRepository userSubscriptionRepository,
                                            SubscriptionService subscriptionService) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.subscriptionService = subscriptionService;
    }

    @Override
    @Transactional
    public PurchaseRequestResponseDto createPurchaseRequest(Long courseId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        if (!com.edtech.edtech_backend.model.ContentAccessType.PREMIUM.equals(course.getAccessType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Purchase request only allowed for PREMIUM courses");
        }

        // Check existing access
        boolean hasAccess = userSubscriptionRepository.findByUserAndCourse(user, course).isPresent();
        if (hasAccess) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already have access to this course");
        }

        // Check existing pending request
        requestRepository.findByUserAndCourseAndStatus(user, course, PurchaseRequestStatus.PENDING)
                .ifPresent(r -> { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Purchase request is already pending"); });

        CoursePurchaseRequest req = new CoursePurchaseRequest();
        req.setUser(user);
        req.setCourse(course);
        req.setCourseAmount(course.getDiscountedPrice() != null ? course.getDiscountedPrice() : course.getOriginalPrice());
        req.setStatus(PurchaseRequestStatus.PENDING);
        req = requestRepository.save(req);

        String message = buildWhatsAppMessage(user, course, req.getCourseAmount());
        String url = buildWhatsAppUrl(message);

        return new PurchaseRequestResponseDto(req.getId(), url);
    }

    private String buildWhatsAppMessage(User user, Course course, BigDecimal amount) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello Admin,\n\n");
        sb.append("I want to purchase a course.\n\n");
        sb.append("Student ID: ").append(user.getId()).append("\n");
        sb.append("Student Name: ").append(user.getName()).append("\n");
        sb.append("Course ID: ").append(course.getId()).append("\n");
        sb.append("Course Name: ").append(course.getTitle()).append("\n");
        sb.append("Course Amount: ₹").append(amount).append("\n\n");
        sb.append("Please provide access to this course.\n\nThanks.");
        return sb.toString();
    }

    private String buildWhatsAppUrl(String message) {
        try {
            String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
            String cleanedNumber = whatsappAdminNumber.replaceAll("[^0-9]", "");
            return String.format("https://wa.me/%s?text=%s", cleanedNumber, encoded);
        } catch (UnsupportedEncodingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to encode WhatsApp message");
        }
    }

    @Override
    public List<CoursePurchaseRequestDto> listPurchaseRequests(PurchaseRequestStatus status) {
        List<CoursePurchaseRequest> list = status == null ? requestRepository.findAll() : requestRepository.findByStatus(status);
        return list.stream().map(r -> new CoursePurchaseRequestDto(
                r.getId(),
                r.getUser().getId(),
                r.getUser().getName(),
                r.getCourse().getId(),
                r.getCourse().getTitle(),
                r.getCourseAmount(),
                r.getStatus(),
                r.getCreatedAt()
        )).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CoursePurchaseRequestDto approveRequest(Long requestId, Long adminUserId, int durationDays) {
        CoursePurchaseRequest req = requestRepository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase request not found"));
        if (req.getStatus() != PurchaseRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending requests can be approved");
        }

        User admin = userRepository.findById(adminUserId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found"));

        // Grant access using existing subscription activation
        com.edtech.edtech_backend.dto.SubscriptionActivationRequest activation = new com.edtech.edtech_backend.dto.SubscriptionActivationRequest();
        activation.setUserId(req.getUser().getId());
        activation.setCourseId(req.getCourse().getId());
        activation.setDurationInDays(durationDays);

        userRepository.findById(req.getUser().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        subscriptionService.activateSubscription(activation, admin);

        req.setStatus(PurchaseRequestStatus.APPROVED);
        req.setApprovedByAdmin(admin);
        req.setApprovedAt(LocalDateTime.now());
        requestRepository.save(req);

        return new CoursePurchaseRequestDto(
                req.getId(),
                req.getUser().getId(),
                req.getUser().getName(),
                req.getCourse().getId(),
                req.getCourse().getTitle(),
                req.getCourseAmount(),
                req.getStatus(),
                req.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public CoursePurchaseRequestDto rejectRequest(Long requestId, Long adminUserId) {
        CoursePurchaseRequest req = requestRepository.findById(requestId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase request not found"));
        if (req.getStatus() != PurchaseRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending requests can be rejected");
        }

        User admin = userRepository.findById(adminUserId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found"));

        req.setStatus(PurchaseRequestStatus.REJECTED);
        requestRepository.save(req);

        return new CoursePurchaseRequestDto(
                req.getId(),
                req.getUser().getId(),
                req.getUser().getName(),
                req.getCourse().getId(),
                req.getCourse().getTitle(),
                req.getCourseAmount(),
                req.getStatus(),
                req.getCreatedAt()
        );
    }
}
