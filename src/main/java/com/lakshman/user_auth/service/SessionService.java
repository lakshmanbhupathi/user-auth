package com.lakshman.user_auth.service;

import com.lakshman.user_auth.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface SessionService {
    void invalidateSession(String token);

    void cleanupExpiredSessions();

    String createSession(User user, HttpServletRequest httpRequest);
}
