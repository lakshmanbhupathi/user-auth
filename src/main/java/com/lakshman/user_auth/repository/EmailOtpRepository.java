package com.lakshman.user_auth.repository;

import com.lakshman.user_auth.entity.EmailOtp;
import com.lakshman.user_auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    Optional<EmailOtp> findByEmailAndOtpCodeAndVerifiedFalseAndExpiresAtAfter(
        String email, String otpCode, LocalDateTime currentTime
    );
    Optional<EmailOtp> findByUserAndPurposeAndVerifiedFalseAndExpiresAtAfter(
        User user, String purpose, LocalDateTime currentTime
    );
    void deleteByExpiresAtBefore(LocalDateTime expirationTime);
}
