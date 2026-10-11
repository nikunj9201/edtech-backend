package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.entity.TestSeries;
import com.edtech.edtech_backend.entity.User;
import com.edtech.edtech_backend.entity.UserTestSeriesSubscription;
import com.edtech.edtech_backend.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTestSeriesSubscriptionRepository extends JpaRepository<UserTestSeriesSubscription, Long> {
    List<UserTestSeriesSubscription> findByUserOrderByEndDateDesc(User user);
    List<UserTestSeriesSubscription> findByUserAndTestSeries(User user, TestSeries testSeries);
    Optional<UserTestSeriesSubscription> findByUserAndTestSeriesAndStatus(User user, TestSeries testSeries, SubscriptionStatus status);
}
