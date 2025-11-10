package com.lakshman.user_auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_audits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestAudit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "request_path", nullable = false, length = 500)
    private String requestPath;
    
    @Column(nullable = false, length = 10)
    private String method;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    @Column(name = "status_code")
    private Integer statusCode;
    
    @Column(name = "response_time_ms")
    private Long responseTimeMs;
    
    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;
    
    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
