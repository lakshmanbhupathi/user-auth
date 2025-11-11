package com.lakshman.user_auth.scheduler;

import com.lakshman.user_auth.service.EmailOTPService;
import com.lakshman.user_auth.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SessionCleanupScheduler {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private EmailOTPService emailOtpService;

    //TODO move properties
    // Run every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void cleanupExpiredSessions() {
        sessionService.cleanupExpiredSessions();
    }
    
    // Run every hour to clean up expired OTPs
    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredOtps() {
        emailOtpService.cleanupExpiredOtps();
    }
}
