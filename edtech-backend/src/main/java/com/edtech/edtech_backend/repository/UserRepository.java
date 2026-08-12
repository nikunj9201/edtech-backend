package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.entity.User; // Corrected import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    default Optional<User> findByUsername(String username) {
        return findByEmail(username);
    }
}