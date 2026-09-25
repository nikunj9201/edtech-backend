package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.entity.TestPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestPaperRepository extends JpaRepository<TestPaper, Long> {
    List<TestPaper> findByTestSeriesIdOrderByDisplayOrderAsc(Long testSeriesId);
    List<TestPaper> findByTestSeriesIdAndActiveTrueOrderByDisplayOrderAsc(Long testSeriesId);
}
