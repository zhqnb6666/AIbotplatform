package com.aibotplatform.controller;

import com.aibotplatform.dto.ChangeUserNameRequest;
import com.aibotplatform.model.User;
import com.aibotplatform.repository.UserRepository;
import com.aibotplatform.service.ProfileService;
import com.aibotplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.PropertyValueException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {
    private final UserRepository userRepository;
    private final ProfileService profileService;

    @PostMapping("/change-username")
    public ResponseEntity<?> changeUsername(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ChangeUserNameRequest changeUserNameRequest) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
        }
        String newUsername = changeUserNameRequest.getNewUsername();
        if (newUsername == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("New username is null");
        }
        if (userDetails.getUsername().equals(newUsername)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("New username cannot be the same as the current username");
        }
        if (userRepository.findByUsername(newUsername) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }
        try {
            profileService.changeUsername(userDetails.getUsername(), newUsername);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Username not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
        return ResponseEntity.ok("Username changed successfully to " + newUsername +
                        ", please log in again.");
    }
}
