package com.aibotplatform.controller;


import com.aibotplatform.dto.BotRatingDTO;
import com.aibotplatform.dto.MessageFeedbackDTO;
import com.aibotplatform.dto.UserFeedbackDTO;
import com.aibotplatform.model.BotRating;
import com.aibotplatform.model.MessageFeedback;
import com.aibotplatform.model.UserFeedback;
import com.aibotplatform.service.BotService;
import com.aibotplatform.service.ConversationService;
import com.aibotplatform.service.FeedbackService;
import com.aibotplatform.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Feedback and Rating API", description = "APIs for providing feedback and ratings")
public class FeedbackAndRatingController {
    private final UserService userService;
    private final BotService botService;
    private final ConversationService conversationService;

    private final FeedbackService feedbackService;


    public FeedbackAndRatingController(UserService userService, BotService botService, ConversationService conversationService, FeedbackService feedbackService) {
        this.userService = userService;
        this.botService = botService;
        this.conversationService = conversationService;
        this.feedbackService = feedbackService;

    }

    @PostMapping("/users/feedback")
    @Operation(summary = "Leave feedback and rating for a user", description = "Provide feedback to a specific user")
    public ResponseEntity<Void> leaveFeedback(
            @RequestBody UserFeedbackDTO feedback) {
        UserFeedback userFeedback = convertToEntity(feedback);
        feedbackService.leaveFeedbackForUser(userFeedback);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/bots/ratings")
    @Operation(summary = "Rate a bot", description = "Rate a specific bot")
    public ResponseEntity<Void> rateBot(
            @RequestBody BotRatingDTO botRatingDTO) {
        BotRating botRating = convertToEntity(botRatingDTO);
        feedbackService.ratingBot(botRating);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/conversations/feedback")
    @Operation(summary = "Provide feedback on a bot response", description = "Leave feedback on a specific conversation")
    public ResponseEntity<Void> leaveFeedbackForMessage(
            @RequestBody MessageFeedbackDTO feedback) {
        MessageFeedback messageFeedback = convertToEntity(feedback);
        feedbackService.leaveFeedbackForMessage(messageFeedback);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    public UserFeedback convertToEntity(UserFeedbackDTO userFeedbackDTO) {
        return new UserFeedback(
                userFeedbackDTO.userFeedbackId(),
                userService.getUserById(userFeedbackDTO.UserId()),
                userService.getUserById(userFeedbackDTO.commenterId()),
                userFeedbackDTO.rating(), userFeedbackDTO.content());
    }

    public MessageFeedback convertToEntity(MessageFeedbackDTO messageFeedbackDTO) {
        return new MessageFeedback(
                messageFeedbackDTO.messageFeedbackId(),
                messageFeedbackDTO.content(),
                conversationService.getMessageById(messageFeedbackDTO.messageId()),
                userService.getUserById(messageFeedbackDTO.CommenterId()),
                messageFeedbackDTO.type());
    }

    public BotRating convertToEntity(BotRatingDTO botRatingDTO) {
        return new BotRating(
                botRatingDTO.botId(),
                botService.getBotById(botRatingDTO.botId()),
                userService.getUserById(botRatingDTO.userId()),
                botRatingDTO.rating());
    }
}
