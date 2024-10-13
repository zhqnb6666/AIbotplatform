package com.aibotplatform.service.impl;

import com.aibotplatform.dto.FeedbackDTO;
import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.UserFeedback;
import com.aibotplatform.repository.FeedbackRepository;
import com.aibotplatform.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public List<FeedbackDTO> getUserFeedback(Long userId) {
        List<UserFeedback> feedbackList = null;
        try {
            feedbackList = feedbackRepository.findByUser_UserId(userId);
        } catch (Exception e) {
            throw new ApiException("User Not Found", HttpStatus.NOT_FOUND);
        }
        return feedbackList.stream()
                .map(feedback -> new FeedbackDTO(feedback.getContent(), feedback.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
