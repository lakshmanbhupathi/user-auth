package com.lakshman.user_auth.service;

import com.lakshman.user_auth.entity.Session;
import com.lakshman.user_auth.repository.SessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Override
    @Transactional
    public void invalidateSession(String token) {
        log.info("Invalidating session: {}", token);
        Session session = sessionRepository.findBySessionToken(token)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        log.info("Session found: {}", session.getId());
        //todo moveToHistory(session, "logout");
        sessionRepository.delete(session);
        log.info("Session deleted: {}", session.getId());
    }
}
