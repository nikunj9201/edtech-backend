package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.model.TestSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestSeriesRepository extends JpaRepository<TestSeries, Long> {
    List<TestSeries> findByCourseId(Long courseId);
}