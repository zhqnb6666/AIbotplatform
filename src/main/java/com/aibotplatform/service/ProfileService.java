package com.aibotplatform.service;

import com.aibotplatform.dto.profileDTO.ProfileResponse;
import com.aibotplatform.dto.profileDTO.UserFeedbackResponse;
import com.aibotplatform.dto.profileDTO.UserStatisticsResponse;
import com.aibotplatform.model.User;

import java.util.List;

public interface ProfileService {
    ProfileResponse getUserProfile(String username);
    ProfileResponse getUserProfileById(Long id);
    void updateUsername(String currentUsername, String newUsername);
    void updateAvatarUrl(String username, String avatarUrl);
    void updateBio(String username, String bio);
    UserStatisticsResponse getUserStatistics(String username);
}
