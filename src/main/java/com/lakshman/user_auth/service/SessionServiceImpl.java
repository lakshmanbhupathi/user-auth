package com.lakshman.user_auth.service;

import com.lakshman.user_auth.entity.Session;
import com.lakshman.user_auth.entity.SessionHistory;
import com.lakshman.user_auth.repository.SessionHistoryRepository;
import com.lakshman.user_auth.repository.SessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionHistoryRepository sessionHistoryRepository;

    @Value("${session.timeout.minutes:30}")
    private int sessionTimeoutMinutes;

    @Override
    @Transactional
    public void invalidateSession(final String token) {
        log.info("Invalidating session: {}", token);
        Session session = sessionRepository.findBySessionToken(token)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        log.info("Session found:: {}", session.getId());
        moveToHistory(session, "logout");
        sessionRepository.delete(session);
        log.info("session deleted:: {}", session.getId());
    }

    @Override
    @Transactional
    public void cleanupExpiredSessions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(sessionTimeoutMinutes);
        List<Session> expiredSessions = sessionRepository.findByLastAccessedAtBeforeAndIsActiveTrue(cutoffTime);
        log.info("Found {} expired sessions", expiredSessions.size());
        for (Session session : expiredSessions) {
            moveToHistory(session, "timeout");
            sessionRepository.delete(session);
            log.info("Session deleted: {}", session.getId());
        }
        log.info("Cleaned up {} expired sessions", expiredSessions.size());
        System.out.println("Cleaned up " + expiredSessions.size() + " expired sessions");
    }

    private void moveToHistory(Session session, final String reason) {
        log.info("Moving session to history: {}", session.getId());
        SessionHistory history = SessionHistory.builder()
                .userId(session.getUser().getId())
                .sessionToken(session.getSessionToken())
                .createdAt(session.getCreatedAt())
                .lastAccessedAt(session.getLastAccessedAt())
                .expiresAt(session.getExpiresAt())
                .endedAt(LocalDateTime.now())
                .endReason(reason)
                .ipAddress(session.getIpAddress())
                .userAgent(session.getUserAgent())
                .build();

        sessionHistoryRepository.save(history);
        log.info("Session history saved: {}", history.getId());
    }
}
