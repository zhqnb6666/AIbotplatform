package com.aibotplatform.service;

import com.aibotplatform.dto.feedbackDTO.*;
import com.aibotplatform.model.BotRating;
import com.aibotplatform.model.MessageFeedback;
import com.aibotplatform.model.UserFeedback;

import java.util.List;

public interface FeedbackService {
    List<UserFeedback> getUserFeedback(Long userId);

    void leaveFeedbackForMessage(MessageFeedbackRequest messageFeedbackRequest, String commenterName);

    void leaveFeedbackForUser(UserFeedbackRequest userFeedbackRequest, String commenterName);

    void ratingBot(BotRatingRequest botRatingRequest, String commenterName);

    BotRatingResponse getBotRating(Long botId);

    List<BotRatingDetailResponse> getBotRatingDetails(Long botId);
}
