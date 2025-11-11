package com.lakshman.user_auth.service;

import com.lakshman.user_auth.dto.ApiResponse;
import com.lakshman.user_auth.dto.AuthResponse;
import com.lakshman.user_auth.dto.LoginRequest;
import com.lakshman.user_auth.dto.SignupRequest;
import com.lakshman.user_auth.dto.VerifyOtpRequest;
import com.lakshman.user_auth.entity.User;
import com.lakshman.user_auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailOTPService emailOtpService;

    @Autowired
    private SessionService sessionService;

    @Override
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            log.error("user already exists :: {}", request.getUsername());
            throw new RuntimeException("user already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error(" email already exists :: {}", request.getEmail());
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .emailOtpEnabled(true)
                .gauthEnabled(false)
                .build();

        userRepository.save(user);
        log.info("User saved :: {}", user.getId());

        // Send OTP
        emailOtpService.generateAndSendOtp(user.getEmail(), user, "signup");
        log.info("OTP sent to email :: {}", user.getEmail());

        return new AuthResponse("Signup successful. Please verify your email with OTP.",
                null,
                false,
                user.getEmail());

    }

    @Override
    @Transactional
    public AuthResponse verifySignupOtp(VerifyOtpRequest request) {
        boolean verified = emailOtpService.verifyOtp(request.getEmail(), request.getOtpCode());

        if (!verified) {
            log.error("Invalid or expired OTP");
            throw new RuntimeException("Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User found: {}", user.getId());
        user.setEnabled(true);
        userRepository.save(user);
        log.info("User enabled: {}", user.getId());

        return new AuthResponse("Email verified successfully. You can now login.",
                null,
                false,
                null);
    }

    @Transactional
    @Override
    public AuthResponse login(LoginRequest request) {

        log.info("login request:: {}", request.getUsername());

        final String invalidUsernameOrPasswordMsg = "invalid username or password";
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException(invalidUsernameOrPasswordMsg));

        log.info(" user found :: {}", user.getId());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error(invalidUsernameOrPasswordMsg);
            throw new RuntimeException(invalidUsernameOrPasswordMsg);
        }

        if (!user.getEnabled()) {
            final String msg = "Account not verified. Please verify your email.";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        // Send OTP for login
        emailOtpService.generateAndSendOtp(user.getEmail(), user, "login");
        log.info("OTP sent  to email :: {}", user.getEmail());
        return new AuthResponse("OTP sent to your email. Please verify.",
                null,
                false,
                user.getEmail());
    }

    @Override
    public AuthResponse verifyLoginOtp(VerifyOtpRequest request, HttpServletRequest httpRequest) {
        return null;
    }

    @Transactional
    public ApiResponse logout(String token) {
        log.info("Logout request: {}", token);
        sessionService.invalidateSession(token);
        log.info("Session invalidated: {}", token);
        return new ApiResponse("Logout successful", true);
    }

}
