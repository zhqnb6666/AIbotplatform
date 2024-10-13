package com.aibotplatform.service;

import com.aibotplatform.dto.FeedbackDTO;

import java.util.List;

public interface FeedbackService {
    List<FeedbackDTO> getUserFeedback(Long userId);
}
