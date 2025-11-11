package com.lakshman.user_auth.service;

import com.lakshman.user_auth.entity.EmailOtp;
import com.lakshman.user_auth.entity.User;
import com.lakshman.user_auth.repository.EmailOtpRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
public class EmailOTPServiceImpl implements EmailOTPService{

    @Autowired
    private EmailOtpRepository emailOtpRepository;

    @Value("${otp.expiration.minutes:10}")
    private int otpExpirationMinutes;

    @Override
    @Transactional
    public String generateAndSendOtp(String email, User user, String purpose) {
        log.info("Generating and sending OTP for email: {} and user: {} and purpose: {}", email, user.getId(), purpose);
        String otpCode = generateOtpCode();

        EmailOtp emailOtp = EmailOtp.builder()
                .email(email)
                .user(user)
                .otpCode(otpCode)
                .purpose(purpose)
                .verified(false)
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                .build();

        emailOtp = emailOtpRepository.save(emailOtp);
        log.info("OTP saved: {}", emailOtp.getId());

        // Mock email sending - log to console
        // TODO can call separate integration to send mail
        log.info("EMAIL OTP for {}", email);
        log.info("OTP Code: {}", otpCode);
        log.info("Purpose: {}", purpose);
        log.info("Expires at: {}", emailOtp.getExpiresAt());

        return otpCode;
    }

    @Override
    @Transactional
    public boolean verifyOtp(String email, String otpCode) {
        log.info("Verifying OTP for email: {} and otpCode: {}", email, otpCode);
        EmailOtp emailOtp = emailOtpRepository
                .findByEmailAndOtpCodeAndVerifiedFalseAndExpiresAtAfter(
                        email, otpCode, LocalDateTime.now())
                .orElse(null);
        log.info("Email OTP found: {}", emailOtp != null);
        if (emailOtp != null) {
            emailOtp.setVerified(true);
            emailOtp.setVerifiedAt(LocalDateTime.now());
            emailOtpRepository.save(emailOtp);
            log.info("OTP verified: {}", emailOtp.getId());
            return true;
        }

        log.info("OTP not verified");
        return false;
    }

    private String generateOtpCode() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Transactional
    @Override
    public void cleanupExpiredOtps() {
        log.info("Cleaning up expired OTPs");
        emailOtpRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        log.info("Expired OTPs cleaned up");
    }

}
