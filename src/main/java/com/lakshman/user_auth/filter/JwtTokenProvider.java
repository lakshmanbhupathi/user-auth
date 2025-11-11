package com.lakshman.user_auth.filter;

public interface JwtTokenProvider {
    String generateToken(Long userId, Long sessionId);

    Long getUserIdFromToken(String token);

    Long getSessionIdFromToken(String token);

    boolean validateToken(String token);
}
