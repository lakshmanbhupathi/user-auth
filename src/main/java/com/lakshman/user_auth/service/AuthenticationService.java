package com.lakshman.user_auth.service;

import com.lakshman.user_auth.dto.ApiResponse;
import com.lakshman.user_auth.dto.AuthResponse;
import com.lakshman.user_auth.dto.LoginRequest;
import com.lakshman.user_auth.dto.SignupRequest;
import com.lakshman.user_auth.dto.VerifyOtpRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface AuthenticationService {
    AuthResponse signup(@Valid SignupRequest request);

    AuthResponse verifySignupOtp(@Valid VerifyOtpRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse verifyLoginOtp(@Valid VerifyOtpRequest request, HttpServletRequest httpRequest);

    ApiResponse logout(String token);
}
