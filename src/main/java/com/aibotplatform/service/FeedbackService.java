package com.aibotplatform.service;

import com.aibotplatform.dto.UserFeedbackDTO;
import com.aibotplatform.model.BotRating;
import com.aibotplatform.model.MessageFeedback;
import com.aibotplatform.model.UserFeedback;

import java.util.List;

public interface FeedbackService {
    List<UserFeedback> getUserFeedback(Long userId);

    void leaveFeedbackForMessage(MessageFeedback feedback);

    void leaveFeedbackForUser(UserFeedback feedback);

    void ratingBot(BotRating botRating);
}
