package com.lakshman.user_auth.service;

import com.lakshman.user_auth.dto.ApiResponse;
import com.lakshman.user_auth.dto.AuthResponse;
import com.lakshman.user_auth.dto.GAuthSetupResponse;
import com.lakshman.user_auth.dto.LoginRequest;
import com.lakshman.user_auth.dto.SignupRequest;
import com.lakshman.user_auth.dto.VerifyGAuthRequest;
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

    @Autowired
    private GoogleAuthenticatorService googleAuthenticatorService;

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
        log.info("Verifying login OTP for email: {} and otpCode: {}", request.getEmail(), request.getOtpCode());
        boolean verified = emailOtpService.verifyOtp(request.getEmail(), request.getOtpCode());

        if (!verified) {
            String msg = "Invalid or expired OTP";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if GAuth is enabled
        if (user.getGauthEnabled()) {
            log.info("GAuth is enabled for user: {}", user.getId());
            return new AuthResponse("Email OTP verified. Please verify Google Authenticator code.", null, true, user.getEmail());
        }

        // Create session and return token
        String token = sessionService.createSession(user, httpRequest);
        log.info("Session created for user: {}", user.getId());
        return new AuthResponse("Login successful", token, false, null);
    }

    @Override
    @Transactional
    public ApiResponse logout(String token) {
        log.info("Logout request: {}", token);
        sessionService.invalidateSession(token);
        log.info("Session invalidated: {}", token);
        return new ApiResponse("Logout successful", true);
    }

    // TODO 2FA into another service
    @Transactional
    @Override
    public AuthResponse verifyGAuth(VerifyGAuthRequest request, HttpServletRequest httpRequest) {
        log.info("Verifying GAuth for email: {} and code: {}", request.getEmail(), request.getCode());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User found: {}", user.getId());
        int code;
        try {
            code = Integer.parseInt(request.getCode());
        } catch (NumberFormatException e) {
            log.error("Invalid verification code");
            throw new RuntimeException("Invalid verification code");
        }

        boolean verified = googleAuthenticatorService.verifyCode(user.getId(), code);
        log.info("GAuth verified: {}", verified);

        if (!verified) {
            log.error("Invalid Google Authenticator code");
            throw new RuntimeException("Invalid Google Authenticator code");
        }

        // Create session and return token
        String token = sessionService.createSession(user, httpRequest);
        log.info("Session created for user: {}", user.getId());
        return new AuthResponse("Login successful", token, false, null);
    }

    @Transactional
    @Override
    public GAuthSetupResponse setupGAuth(Long userId) {
        log.info("Setting up GAuth for user: {}", userId);
        String secretKey = googleAuthenticatorService.setupGoogleAuthenticator(userId);
        log.info("Secret key generated: {}", secretKey);
        String qrCodeUrl = googleAuthenticatorService.generateQRCodeUrl(userId, secretKey);
        log.info("QR code URL generated: {}", qrCodeUrl);
        return new GAuthSetupResponse(
                "Scan the QR code with Google Authenticator app and verify with a code to enable 2FA",
                secretKey,
                qrCodeUrl
        );
    }

    @Transactional
    @Override
    public ApiResponse enableGAuth(Long userId, String code) {
        log.info("Enabling GAuth for user: {} and code: {}", userId, code);
        int verificationCode;
        try {
            verificationCode = Integer.parseInt(code);
        } catch (NumberFormatException e) {
            log.error("Invalid verification code");
            throw new RuntimeException("Invalid verification code");
        }

        boolean verified = googleAuthenticatorService.verifyAndEnableGAuth(userId, verificationCode);

        if (!verified) {
            log.error("Invalid Google Authenticator code");
            throw new RuntimeException("Invalid Google Authenticator code");
        }

        log.info("GAuth enabled successfully");
        return new ApiResponse("Google Authenticator enabled successfully", true);
    }

}
