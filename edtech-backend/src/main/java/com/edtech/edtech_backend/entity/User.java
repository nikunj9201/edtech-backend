package com.edtech.edtech_backend.entity;

import com.edtech.edtech_backend.model.Role; // Added this import
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "contact_number")
    private String contactNumber;

    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}