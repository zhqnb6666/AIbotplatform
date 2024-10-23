package com.aibotplatform.service;

import com.aibotplatform.dto.feedbackDTO.BotRatingRequest;
import com.aibotplatform.dto.feedbackDTO.MessageFeedbackRequest;
import com.aibotplatform.dto.feedbackDTO.UserFeedbackRequest;
import com.aibotplatform.model.BotRating;
import com.aibotplatform.model.MessageFeedback;
import com.aibotplatform.model.UserFeedback;

import java.util.List;

public interface FeedbackService {
    List<UserFeedback> getUserFeedback(Long userId);

    void leaveFeedbackForMessage(MessageFeedbackRequest messageFeedbackRequest, String commenterName);

    void leaveFeedbackForUser(UserFeedbackRequest userFeedbackRequest, String commenterName);

    void ratingBot(BotRatingRequest botRatingRequest, String commenterName);
}
