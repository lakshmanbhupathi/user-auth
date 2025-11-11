package com.lakshman.user_auth.db;

import com.lakshman.user_auth.entity.User;
import com.lakshman.user_auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    //TODO move to Flyway Migration

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing data...");
        // Create test users if they don't exist
        createTestUserIfNotExists("testuser1", "test1@example.com", "Test123!");
        createTestUserIfNotExists("testuser2", "test2@example.com", "Test123!");
        
        log.info("==============================================");
        log.info("TEST USER CREDENTIALS");
        log.info("==============================================");
        log.info("User 1:");
        log.info("  Username: testuser1");
        log.info("  Email: test1@example.com");
        log.info("  Password: Test123!");
        log.info("----------------------------------------------");
        log.info("User 2:");
        log.info("  Username: testuser2");
        log.info("  Email: test2@example.com");
        log.info("  Password: Test123!");
        log.info("----------------------------------------------");
        log.info("==============================================");
        log.info("Note: Users are pre-created and enabled.");
        log.info("You can use them directly for login (OTP verification still required).");
        log.info("==============================================\n");
    }
    
    private void createTestUserIfNotExists(String username, String email, String password) {
        log.info("Creating test user: {} if not exists", username);
        if (!userRepository.existsByUsername(username)) {
            User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .emailOtpEnabled(true)
                .gauthEnabled(false)
                .build();
            userRepository.save(user);
            log.info("Created test user: {}", username);
        }
    }
}
