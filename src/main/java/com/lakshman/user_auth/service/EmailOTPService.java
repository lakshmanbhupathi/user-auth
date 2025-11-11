package com.lakshman.user_auth.service;

import com.lakshman.user_auth.entity.User;
import org.springframework.transaction.annotation.Transactional;

public interface EmailOTPService {
    String generateAndSendOtp(String email, User user, String signup);
    boolean verifyOtp(String email, String otpCode);

    @Transactional
    void cleanupExpiredOtps();
}
