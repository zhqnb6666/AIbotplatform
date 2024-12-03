package com.aibotplatform.controller;


import com.aibotplatform.dto.feedbackDTO.BotRatingRequest;
import com.aibotplatform.dto.feedbackDTO.MessageFeedbackRequest;
import com.aibotplatform.dto.feedbackDTO.UserFeedbackRequest;
import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.MessageFeedback;
import com.aibotplatform.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Feedback and Rating API", description = "APIs for providing feedback and ratings")
public class FeedbackAndRatingController {
    private final FeedbackService feedbackService;

    @PostMapping("/users/feedback")
    @Operation(summary = "Leave feedback and rating for a user", description = "Provide feedback to a specific user")
    public ResponseEntity<?> leaveFeedback(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserFeedbackRequest userFeedbackRequest) {
        try {
            feedbackService.leaveFeedbackForUser(userFeedbackRequest, userDetails.getUsername());
        } catch (ApiException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/bots/ratings")
    @Operation(summary = "Rate a bot", description = "Rate a specific bot")
    public ResponseEntity<?> rateBot(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody BotRatingRequest botRatingRequest) {
        try {
            feedbackService.ratingBot(botRatingRequest, userDetails.getUsername());
        } catch (ApiException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/conversations/feedback")
    @Operation(summary = "Provide feedback on a bot response", description = "Leave feedback on a specific conversation")
    public ResponseEntity<?> leaveFeedbackForMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MessageFeedbackRequest feedbackRequest) {
        feedbackService.leaveFeedbackForMessage(feedbackRequest, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/bots/{botId}/ratings")
    @Operation(summary = "Get ratings for a bot", description = "Get all ratings for a specific bot")
    public ResponseEntity<?> getBotRatings(@PathVariable Long botId) {
        return ResponseEntity.ok(feedbackService.getBotRating(botId));
    }
}
