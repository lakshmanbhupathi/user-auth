package com.lakshman.user_auth.service;

import com.lakshman.user_auth.dto.AuthResponse;
import com.lakshman.user_auth.dto.SignupRequest;
import com.lakshman.user_auth.dto.VerifyOtpRequest;
import com.lakshman.user_auth.entity.User;
import com.lakshman.user_auth.repository.UserRepository;
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
    public AuthResponse verifySignupOtp(VerifyOtpRequest request) {
        return null;
    }
}
