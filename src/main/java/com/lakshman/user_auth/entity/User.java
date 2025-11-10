package com.lakshman.user_auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Authentication fields
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean enabled = false;

    @Column(name = "email_otp_enabled")
    private Boolean emailOtpEnabled = true;

    @Column(name = "gauth_enabled")
    private Boolean gauthEnabled = false;

    // Profile fields (from UserProfile)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    // Google Authenticator fields (from GoogleAuthenticator)
    @Column(name = "gauth_secret_key", unique = true)
    private String gauthSecretKey;

    @Column(name = "gauth_enabled_at")
    private LocalDateTime gauthEnabledAt;

    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
