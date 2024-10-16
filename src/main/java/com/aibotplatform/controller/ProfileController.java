package com.aibotplatform.controller;

import com.aibotplatform.dto.ChangeBioRequest;
import com.aibotplatform.dto.ChangeUserNameRequest;
import com.aibotplatform.dto.ProfileResponse;
import com.aibotplatform.model.User;
import com.aibotplatform.repository.UserRepository;
import com.aibotplatform.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {
    // test
    private final UserRepository userRepository;
    private final ProfileService profileService;

    private final String uploadDir = "avatars/";

    @GetMapping
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        User user = userRepository.findByUsername(userDetails.getUsername());
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setUsername(user.getUsername());
        profileResponse.setCredits(user.getCredits());
        profileResponse.setRole(user.getRole().toString());
        profileResponse.setAvatarUrl(user.getAvatarUrl());
        profileResponse.setEmail(user.getEmail());
        profileResponse.setBio(user.getBio());
        return ResponseEntity.ok(profileResponse);
    }

    @PostMapping("/change-username")
    public ResponseEntity<?> changeUsername(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestBody ChangeUserNameRequest changeUserNameRequest) {
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
            profileService.updateUsername(userDetails.getUsername(), newUsername);
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

    @PostMapping("/change-avatar")
    public ResponseEntity<?> changeAvatar(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam("file") MultipartFile file){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File must be an image");
        }

        String newFileName = UUID.randomUUID().toString() + getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            Path path = Paths.get(uploadDir, newFileName);
            Files.createDirectories(path.getParent()); // 确保目录存在
            file.transferTo(path);
            profileService.updateAvatarUrl(userDetails.getUsername(), newFileName);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file: " + e.getMessage());
        }

        return ResponseEntity.ok("Avatar changed successfully.");
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.'));
    }

    @PostMapping("/change-bio")
    public ResponseEntity<?> changeBio(@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestBody ChangeBioRequest changeBioRequest) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
        }
        try {
            profileService.updateBio(userDetails.getUsername(), changeBioRequest.getNewBio());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Username not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
        return ResponseEntity.ok("Bio changed successfully.");
    }

}
