package com.lakshman.user_auth.service;

import com.lakshman.user_auth.dto.ProfileResponse;
import com.lakshman.user_auth.dto.UpdateProfileRequest;

public interface UserService {
    ProfileResponse getUserProfile(Long userId);

    ProfileResponse updateUserProfile(Long userId, UpdateProfileRequest request);
}
