package com.lakshman.user_auth.controller;

import com.lakshman.user_auth.dto.ApiResponse;
import com.lakshman.user_auth.dto.AuthResponse;
import com.lakshman.user_auth.dto.LoginRequest;
import com.lakshman.user_auth.dto.SignupRequest;
import com.lakshman.user_auth.dto.VerifyOtpRequest;
import com.lakshman.user_auth.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/{ver}/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request,
                                               @PathVariable("ver") String ver) {
        log.info("Signup request: {}", request.getUsername());
        try {
            AuthResponse response = authenticationService.signup(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Signup error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(),
                            null,
                            false,
                            null));
        }
    }

    @PostMapping("/verify-signup-otp")
    public ResponseEntity<AuthResponse> verifySignupOtp(@Valid @RequestBody VerifyOtpRequest request,
                                                        @PathVariable("ver") String ver) {
        log.info("Verify signup OTP request: {}", request.getEmail());
        try {
            AuthResponse response = authenticationService.verifySignupOtp(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Verify signup OTP error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(),
                            null,
                            false,
                            null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request: {}", request.getUsername());
        try {
            AuthResponse response = authenticationService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in login api :: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(),
                            null,
                            false,
                            null));
        }
    }

    @PostMapping("/verify-login-otp")
    public ResponseEntity<AuthResponse> verifyLoginOtp(@Valid @RequestBody VerifyOtpRequest request,
                                                       HttpServletRequest httpRequest) {
        log.info("Verify login OTP request: {}", request.getEmail());
        try {
            AuthResponse response = authenticationService.verifyLoginOtp(request, httpRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Verify login OTP error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(e.getMessage(), null, false, null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {
        log.info("Logout request");
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("No token provided", false));
            }

            String token = authHeader.substring(7);
            ApiResponse response = authenticationService.logout(token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Logout error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }
}
