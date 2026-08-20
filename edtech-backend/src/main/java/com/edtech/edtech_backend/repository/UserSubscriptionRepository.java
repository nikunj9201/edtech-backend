package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.entity.Course;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.entity.UserSubscription;
import com.edtech.edtech_backend.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findByUserAndStatus(User user, SubscriptionStatus status);
    List<UserSubscription> findByUserOrderByEndDateDesc(User user);
    Optional<UserSubscription> findByUserAndCourse(User user, Course course);
}
