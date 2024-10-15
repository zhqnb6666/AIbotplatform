package com.aibotplatform.service.impl;

import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.BotRating;
import com.aibotplatform.model.MessageFeedback;
import com.aibotplatform.model.UserFeedback;
import com.aibotplatform.repository.BotRatingRepository;
import com.aibotplatform.repository.MessageFeedbackRepository;
import com.aibotplatform.repository.UserFeedbackRepository;
import com.aibotplatform.service.FeedbackService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final UserFeedbackRepository userFeedbackRepository;
    private final MessageFeedbackRepository messageFeedbackRepository;

    private final BotRatingRepository botRatingRepository;

    public FeedbackServiceImpl(UserFeedbackRepository userFeedbackRepository, MessageFeedbackRepository messageFeedbackRepository, BotRatingRepository botRatingRepository) {
        this.userFeedbackRepository = userFeedbackRepository;
        this.messageFeedbackRepository = messageFeedbackRepository;
        this.botRatingRepository = botRatingRepository;
    }

    @Override
    public List<UserFeedback> getUserFeedback(Long userId) {
        try {
            return userFeedbackRepository.findByUser_UserId(userId);
        } catch (Exception e) {
            throw new ApiException("User feedback not found", HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public void leaveFeedbackForMessage(MessageFeedback messageFeedback) {
        messageFeedback.setMessageFeedbackId(null);
        messageFeedback.setCreatedAt(Timestamp.from(Instant.now()));
        try {
            messageFeedbackRepository.save(messageFeedback);
        } catch (Exception e) {
            throw new ApiException("Message feedback not saved:" + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void leaveFeedbackForUser(UserFeedback userFeedback) {
        userFeedback.setFeedbackId(null);
        userFeedback.setCreatedAt(Timestamp.from(Instant.now()));
        try {
            userFeedbackRepository.save(userFeedback);
        } catch (Exception e) {
            throw new ApiException("User feedback not saved:" + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void ratingBot(BotRating botRating) {
        botRating.setRatingId(null);
        botRating.setCreatedAt(Timestamp.from(Instant.now()));
        try {
            botRatingRepository.save(botRating);
        } catch (Exception e) {
            throw new ApiException("Bot rating not saved:" + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
