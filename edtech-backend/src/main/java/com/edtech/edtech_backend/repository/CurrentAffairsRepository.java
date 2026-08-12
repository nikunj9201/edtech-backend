package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.entity.CurrentAffairs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentAffairsRepository extends JpaRepository<CurrentAffairs, Long> {
}