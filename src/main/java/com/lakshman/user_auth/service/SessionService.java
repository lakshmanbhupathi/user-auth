package com.lakshman.user_auth.service;

public interface SessionService {
    void invalidateSession(String token);
}
