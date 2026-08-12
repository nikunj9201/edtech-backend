package com.edtech.edtech_backend.entity;

import com.edtech.edtech_backend.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "contact_number")
    private String contactNumber;

    private String country;

    private String city;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}