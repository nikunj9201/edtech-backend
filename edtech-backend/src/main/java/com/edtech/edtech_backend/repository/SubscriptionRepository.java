package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.entity.Subscription; // Corrected import
import com.edtech.edtech_backend.entity.User;         // Corrected import
import com.edtech.edtech_backend.entity.Course;       // Corrected import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserAndCourse(User user, Course course);
    List<Subscription> findByUser(User user);
    List<Subscription> findByUserId(Long userId);
}