package com.edtech.edtech_backend.repository;
import com.edtech.edtech_backend.entity.PreviousYearPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreviousYearPaperRepository extends JpaRepository<PreviousYearPaper, Long> {
}