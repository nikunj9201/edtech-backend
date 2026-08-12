package com.edtech.edtech_backend.repository;

import com.edtech.edtech_backend.model.ERole;
import com.edtech.edtech_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
  Optional<Role> findByName(ERole name);
}
