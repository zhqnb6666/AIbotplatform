package com.aibotplatform.service.impl;

import com.aibotplatform.dto.feedbackDTO.BotRatingRequest;
import com.aibotplatform.dto.feedbackDTO.MessageFeedbackRequest;
import com.aibotplatform.dto.feedbackDTO.UserFeedbackRequest;
import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.*;
import com.aibotplatform.repository.BotRatingRepository;
import com.aibotplatform.repository.MessageFeedbackRepository;
import com.aibotplatform.repository.MessageRepository;
import com.aibotplatform.repository.UserFeedbackRepository;
import com.aibotplatform.service.BotService;
import com.aibotplatform.service.FeedbackService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final UserFeedbackRepository userFeedbackRepository;
    private final MessageFeedbackRepository messageFeedbackRepository;
    private final BotRatingRepository botRatingRepository;
    private final MessageRepository messageRepository;
    private final UserServiceImpl userService;
    private final BotService botService;
    private final EmailServiceImpl emailService;

    @Override
    public List<UserFeedback> getUserFeedback(Long userId) {
        try {
            return userFeedbackRepository.findByUser_UserId(userId);
        } catch (Exception e) {
            throw new ApiException("User feedback not found", HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public void leaveFeedbackForMessage(MessageFeedbackRequest messageFeedbackRequest, String commenterName) throws ApiException {
        MessageFeedback messageFeedback = new MessageFeedback();
        messageFeedback.setMessageFeedbackId(null);
        User commenter = userService.getUserByName(commenterName);
        Message message = messageRepository.findById(messageFeedbackRequest.messageId())
                .orElseThrow(() -> new ApiException("Message not found", HttpStatus.NOT_FOUND));
        messageFeedback.setMessage(message);
        messageFeedback.setCommenter(commenter);
        messageFeedback.setContent(messageFeedbackRequest.content());
        messageFeedback.setFeedbackType(messageFeedbackRequest.type());
        messageFeedback.setCreatedAt(Timestamp.from(Instant.now()));
        try {
            messageFeedbackRepository.save(messageFeedback);
        } catch (Exception e) {
            throw new ApiException("Message feedback not saved:" + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void leaveFeedbackForUser(
            UserFeedbackRequest userFeedbackRequest,
            String commenterName) throws ApiException {
        UserFeedback userFeedback = new UserFeedback();
        User user = userService.getUserById(userFeedbackRequest.userId());
        User commenter = userService.getUserByName(commenterName);
        if (commenter == null || user == null) {
            throw new ApiException("User or commenter not found", HttpStatus.NOT_FOUND);
        }
        if (commenter.getUserId().equals(user.getUserId())) {
            throw new ApiException("You can't leave feedback for yourself", HttpStatus.BAD_REQUEST);
        }
        userFeedback.setUser(user);
        userFeedback.setCommenter(commenter);
        userFeedback.setContent(userFeedbackRequest.content());
        userFeedback.setRating(userFeedbackRequest.rating());
        userFeedback.setFeedbackId(null);
        userFeedback.setCreatedAt(Timestamp.from(Instant.now()));
        try {
            userFeedbackRepository.save(userFeedback);
        } catch (Exception e) {
            throw new ApiException("User feedback not saved:" + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        emailService.sendEmail(user.getEmail(), "AI bot platform feedback from " + commenterName,
                "rating: " + userFeedbackRequest.rating() + "<br>content: " + userFeedbackRequest.content());
    }

    @Override
    public void ratingBot(BotRatingRequest botRatingRequest,
                          String commenterName) {
        BotRating botRating = new BotRating();
        botRating.setRatingId(null);
        Bot bot = botService.getBotById(botRatingRequest.botId());
        botRating.setBot(bot);
        User user = userService.getUserByName(commenterName);
        botRating.setUser(user);
        botRating.setRating(botRatingRequest.rating());
        botRating.setCreatedAt(Timestamp.from(Instant.now()));
        try {
            botRatingRepository.save(botRating);
        } catch (Exception e) {
            throw new ApiException("Bot rating not saved:" + e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
