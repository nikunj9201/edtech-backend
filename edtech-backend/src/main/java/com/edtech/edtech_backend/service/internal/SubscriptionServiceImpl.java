package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.dto.SubscriptionPlanDto;
import com.edtech.edtech_backend.dto.UserSubscriptionDto;
import com.edtech.edtech_backend.entity.Payment;
import com.edtech.edtech_backend.entity.SubscriptionPlan;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.entity.UserSubscription;
import com.edtech.edtech_backend.model.PaymentStatus;
import com.edtech.edtech_backend.model.SubscriptionStatus;
import com.edtech.edtech_backend.repository.PaymentRepository;
import com.edtech.edtech_backend.repository.SubscriptionPlanRepository;
import com.edtech.edtech_backend.repository.UserSubscriptionRepository;
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

    public SubscriptionServiceImpl(SubscriptionPlanRepository planRepository,
                                   UserSubscriptionRepository userSubscriptionRepository,
                                   PaymentRepository paymentRepository) {
        this.planRepository = planRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<SubscriptionPlan> getAllPlans() {
        return planRepository.findByActive(true);
    }

    @Override
    public SubscriptionPlan createPlan(SubscriptionPlanDto planDto) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(planDto.getName());
        plan.setDescription(planDto.getDescription());
        plan.setDurationInDays(planDto.getDurationInDays());
        plan.setPrice(planDto.getPrice());
        plan.setDiscountedPrice(planDto.getDiscountedPrice());
        plan.setActive(planDto.isActive());
        return planRepository.save(plan);
    }

    @Override
    @Transactional
    public UserSubscriptionDto purchaseSubscription(User user, Long planId, String paymentMethod) {
        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        // 1. Create a Payment record in PENDING state
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPlan(plan);
        payment.setAmount(plan.getDiscountedPrice() != null ? plan.getDiscountedPrice() : plan.getPrice());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod(paymentMethod);
        payment = paymentRepository.save(payment);

        // 2. Integrate with Payment Gateway here.
        // For now, we will simulate a successful payment.
        boolean paymentSuccessful = true; // Simulate success
        String transactionId = "txn_" + UUID.randomUUID().toString();

        // 3. Handle payment result
        if (paymentSuccessful) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(transactionId);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);

            // Deactivate any existing active subscription
            userSubscriptionRepository.findByUserAndStatus(user, SubscriptionStatus.ACTIVE)
                .ifPresent(sub -> {
                    sub.setStatus(SubscriptionStatus.CANCELLED);
                    userSubscriptionRepository.save(sub);
                });

            // 4. Create the new UserSubscription
            UserSubscription newSubscription = new UserSubscription();
            newSubscription.setUser(user);
            newSubscription.setPlan(plan);
            newSubscription.setStartDate(LocalDate.now());
            newSubscription.setEndDate(LocalDate.now().plusDays(plan.getDurationInDays()));
            newSubscription.setStatus(SubscriptionStatus.ACTIVE);
            newSubscription.setPaymentId(payment.getId().toString());
            userSubscriptionRepository.save(newSubscription);
            
            return convertToDto(newSubscription);
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Payment failed.");
        }
    }

    @Override
    public UserSubscriptionDto getMySubscription(User user) {
        return userSubscriptionRepository.findByUserAndStatus(user, SubscriptionStatus.ACTIVE)
                .map(this::convertToDto)
                .orElse(createEmptySubscriptionDto());
    }

    @Override
    public List<UserSubscriptionDto> getSubscriptionHistory(User user) {
        return userSubscriptionRepository.findByUserOrderByEndDateDesc(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserSubscriptionDto convertToDto(UserSubscription sub) {
        UserSubscriptionDto dto = new UserSubscriptionDto();
        dto.setSubscribed(sub.getStatus() == SubscriptionStatus.ACTIVE && sub.getEndDate().isAfter(LocalDate.now()));
        dto.setPlanName(sub.getPlan().getName());
        dto.setStartDate(sub.getStartDate());
        dto.setEndDate(sub.getEndDate());
        dto.setStatus(sub.getStatus());
        
        if (dto.isSubscribed()) {
            dto.setDaysRemaining(ChronoUnit.DAYS.between(LocalDate.now(), sub.getEndDate()));
        } else {
            dto.setDaysRemaining(0);
        }
        
        // Find the associated payment to get the transaction ID
        if (sub.getPaymentId() != null) {
            paymentRepository.findById(Long.parseLong(sub.getPaymentId()))
                .ifPresent(p -> dto.setTransactionId(p.getTransactionId()));
        }

        return dto;
    }

    private UserSubscriptionDto createEmptySubscriptionDto() {
        UserSubscriptionDto dto = new UserSubscriptionDto();
        dto.setSubscribed(false);
        dto.setDaysRemaining(0);
        dto.setStatus(null);
        return dto;
    }
}
