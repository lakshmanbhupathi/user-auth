package com.lakshman.user_auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "session_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "session_token", nullable = false, length = 500)
    private String sessionToken;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "last_accessed_at", nullable = false)
    private LocalDateTime lastAccessedAt;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "ended_at", nullable = false)
    private LocalDateTime endedAt;
    
    @Column(name = "end_reason", length = 50)
    private String endReason; // "expired", "logout", "timeout"
    
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
}
