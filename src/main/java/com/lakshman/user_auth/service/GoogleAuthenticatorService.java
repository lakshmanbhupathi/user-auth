package com.lakshman.user_auth.service;

import com.lakshman.user_auth.entity.User;
import com.lakshman.user_auth.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Slf4j
public class GoogleAuthenticatorService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private  com.warrenstrange.googleauth.GoogleAuthenticator gAuth;
    
    @Value("${gauth.issuer:UserAuthApp}")
    private String issuer;

    
    @Transactional
    public String setupGoogleAuthenticator(Long userId) {
        log.info("Setting up GAuth for user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if already enabled
        if (user.getGauthEnabled() && user.getGauthSecretKey() != null) {
            log.error("GAuth already enabled for user: {}", userId);
            throw new RuntimeException("Google Authenticator already enabled");
        }
        
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secretKey = key.getKey();
        log.info("Secret key generated: {}", secretKey);
        
        user.setGauthSecretKey(secretKey);
        userRepository.save(user);
        log.info("Secret key saved: {}", secretKey);
        return secretKey;
    }
    
    public String generateQRCodeUrl(Long userId, String secretKey) {
        log.info("Generating QR code URL for user: {} and secret key: {}", userId, secretKey);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL(
                issuer,
                user.getEmail(),
                new GoogleAuthenticatorKey.Builder(secretKey).build()
        );
    }
    
    @Transactional
    public boolean verifyAndEnableGAuth(Long userId, int code) {
        log.info("Verifying and enabling GAuth for user: {} and code: {}", userId, code);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getGauthSecretKey() == null) {
            log.error("Google Authenticator not set up for user: {}", userId);
            throw new RuntimeException("Google Authenticator not set up");
        }
        
        boolean isValid = gAuth.authorize(user.getGauthSecretKey(), code);
        log.info("GAuth verified: {}", isValid);
        
        if (isValid && !user.getGauthEnabled()) {
            log.info("GAuth enabled for user: {}", userId);
            user.setGauthEnabled(true);
            user.setGauthEnabledAt(LocalDateTime.now());
            userRepository.save(user);
            log.info("GAuth enabled and saved for user: {}", userId);
        }
        
        return isValid;
    }
    
    public boolean verifyCode(Long userId, int code) {
        log.info("Verifying GAuth code for user: {} and code: {}", userId, code);
        User user = userRepository.findById(userId).orElse(null);
        
        if (user == null || !user.getGauthEnabled() || user.getGauthSecretKey() == null) {
            log.error("GAuth not enabled or secret key not set for user: {}", userId);
            return false;
        }
        
        return gAuth.authorize(user.getGauthSecretKey(), code);
    }
}
