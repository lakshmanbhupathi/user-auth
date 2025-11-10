package com.lakshman.user_auth.service;

import com.lakshman.user_auth.dto.ProfileResponse;
import com.lakshman.user_auth.dto.UpdateProfileRequest;
import com.lakshman.user_auth.entity.User;
import com.lakshman.user_auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ProfileResponse.builder().id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .gauthEnabled(user.getGauthEnabled())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .address(user.getAddress()).build();
    }

    @Transactional
    @Override
    public ProfileResponse updateUserProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        userRepository.save(user);

        return getUserProfile(userId);
    }
}
