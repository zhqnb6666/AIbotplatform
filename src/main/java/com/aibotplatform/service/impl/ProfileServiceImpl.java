package com.aibotplatform.service.impl;

import com.aibotplatform.dto.profileDTO.ProfileResponse;
import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.User;
import com.aibotplatform.model.UserFeedback;
import com.aibotplatform.repository.UserRepository;
import com.aibotplatform.service.BotService;
import com.aibotplatform.service.FeedbackService;
import com.aibotplatform.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final BotService botService;
    private final FeedbackService feedbackService;

    @Override
    public ProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username);
        return getProfileResponse(user);
    }

    @Override
    public ProfileResponse getUserProfileById(Long id) throws ApiException {
        User user = userRepository.findByUserId(id);
        if (user == null) {
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        }
        return getProfileResponse(user);
    }

    private ProfileResponse getProfileResponse(User user) {
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setUsername(user.getUsername());
        profileResponse.setCredits(user.getCredits());
        profileResponse.setToken(user.getToken());
        profileResponse.setRole(user.getRole().toString());
        profileResponse.setAvatarUrl(user.getAvatarUrl());
        profileResponse.setEmail(user.getEmail());
        profileResponse.setBio(user.getBio());
        profileResponse.setUserBotList(botService.getUserCustomBots(user.getUserId()));

        profileResponse.setUserFeedbackList(feedbackService.getUserFeedback(user.getUserId()));
        long totalRatingCnt = profileResponse.getUserFeedbackList().size();
        if (totalRatingCnt == 0) {
            profileResponse.setAvgRating(0);
        } else {
            long totalRating = 0;
            for (UserFeedback userFeedback : profileResponse.getUserFeedbackList()) {
                totalRating += userFeedback.getRating() * 1000;
            }
            int avgRating = (int) (totalRating / totalRatingCnt);
            avgRating = avgRating / 10 + (avgRating % 10 >= 5 ? 1 : 0);
            profileResponse.setAvgRating(avgRating);
        }
        return profileResponse;
    }

    @Override
    @Transactional
    public void updateUsername(String currentUsername, String newUsername) throws ApiException {
        if (newUsername == null) {
            throw new ApiException("New username cannot be null", HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        }
        if (currentUsername.equals(newUsername)) {
            throw new ApiException("New username cannot be the same as the current username", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByUsername(newUsername) != null) {
            throw new ApiException("Username already exists", HttpStatus.BAD_REQUEST);
        }
        user.setUsername(newUsername);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateAvatarUrl(String username, String avatarUrl) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void updateBio(String username, String bio) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setBio(bio);
            userRepository.save(user);
        }
    }
}
