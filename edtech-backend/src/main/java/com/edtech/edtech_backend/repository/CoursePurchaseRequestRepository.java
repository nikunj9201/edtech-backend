package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.entity.Course;
import com.edtech.edtech_backend.entity.CoursePurchaseRequest;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.model.PurchaseRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoursePurchaseRequestRepository extends JpaRepository<CoursePurchaseRequest, Long> {
    Optional<CoursePurchaseRequest> findByUserAndCourseAndStatus(User user, Course course, PurchaseRequestStatus status);
    List<CoursePurchaseRequest> findByStatus(PurchaseRequestStatus status);
    List<CoursePurchaseRequest> findByUser(User user);
    Optional<CoursePurchaseRequest> findTopByUserAndCourseOrderByCreatedAtDesc(User user, Course course);
}
