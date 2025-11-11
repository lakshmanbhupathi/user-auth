package com.lakshman.user_auth.service;

import com.lakshman.user_auth.entity.User;

public interface EmailOTPService {
    void generateAndSendOtp(String email, User user, String signup);
}
