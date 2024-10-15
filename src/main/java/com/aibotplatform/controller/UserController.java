package com.aibotplatform.controller;

import com.aibotplatform.dto.UserDTO;
import com.aibotplatform.model.User;
import com.aibotplatform.model.Bot;
import com.aibotplatform.model.UserFeedback;
import com.aibotplatform.service.impl.UserService;
import com.aibotplatform.service.BotService;
import com.aibotplatform.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final BotService botService;

    private final FeedbackService feedbackService;

    public UserController(UserService userService, BotService botService, FeedbackService feedbackService) {
        this.userService = userService;
        this.botService = botService;
        this.feedbackService = feedbackService;
    }

    // GET /api/users/{user_id} - Get user profile
    @GetMapping("/{user_id}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long user_id) {
        User user = userService.getUserById(user_id);
        return new ResponseEntity<>(convertToDTO(user), HttpStatus.OK);
    }

    // PUT /api/users/{user_id} - Update user profile
    @PutMapping("/{user_id}")
    public ResponseEntity<UserDTO> updateUserProfile(@PathVariable Long user_id, @RequestBody @Valid UserDTO userDTO) {
        User user = convertToEntity(userDTO);
        User updatedUser = userService.updateUserProfile(user_id, user);
        return new ResponseEntity<>(convertToDTO(updatedUser), HttpStatus.OK);
    }

    // GET /api/users/{user_id}/bots - Get user's custom bots
    @GetMapping("/{user_id}/bots")
    public ResponseEntity<List<Bot>> getUserBots(@PathVariable Long user_id) {
        List<Bot> userBots = botService.getUserCustomBots(user_id);
        return new ResponseEntity<>(userBots, HttpStatus.OK);
    }

    // GET /api/users/{user_id}/stats - Get user's usage statistics
//    @GetMapping("/{user_id}/stats")
//    public ResponseEntity<UserStatsResponse> getUserStats(@PathVariable Long user_id) {
//        UserStatsResponse userStats = userService.getUserStats(user_id);
//        return new ResponseEntity<>(userStats, HttpStatus.OK);
//    }

    // GET /api/users/{user_id}/feedback - Get feedback for a user
    @GetMapping("/{user_id}/feedback")
    public ResponseEntity<List<UserFeedback>> getUserFeedback(@PathVariable Long user_id) {
        List<UserFeedback> feedbacks = feedbackService.getUserFeedback(user_id);
        return new ResponseEntity<>(feedbacks, HttpStatus.OK);
    }

    // Convert User entity to UserDTO
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCredits(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    // Convert UserDTO to User entity
    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setCredits(userDTO.getCredits());
        return user;
    }
}
