package com.aibotplatform.controller;

import com.aibotplatform.dto.profileDTO.ChangeBioRequest;
import com.aibotplatform.dto.profileDTO.ChangeUserNameRequest;
import com.aibotplatform.dto.profileDTO.ProfileResponse;
import com.aibotplatform.exception.ApiException;
import com.aibotplatform.service.BotService;
import com.aibotplatform.service.FeedbackService;
import com.aibotplatform.service.impl.ProfileServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Profile API", description = "APIS for getting and changing user profile")
public class ProfileController {

    private final ProfileServiceImpl profileService;
    private final BotService botService;
    private final FeedbackService feedbackService;

    private final String uploadDir = "avatars/";

    @GetMapping
    @Operation(summary = "Get user profile", description = "Get user own profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        ProfileResponse userProfile = profileService.getUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user profile", description = "Get user own profile")
    public ResponseEntity<?> getProfileById(@PathVariable Long id) {
        ProfileResponse userProfile = profileService.getUserProfileById(id);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/change-username")
    @Operation(summary = "Change user's username")
    public ResponseEntity<?> changeUsername(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestBody ChangeUserNameRequest changeUserNameRequest) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
        }
        String newUsername = changeUserNameRequest.getNewUsername();
        try {
            profileService.updateUsername(userDetails.getUsername(), newUsername);
        } catch (ApiException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
        return ResponseEntity.ok("Username changed successfully to " + newUsername +
                        ", please log in again.");
    }

    @PutMapping("/change-avatar")
    @Operation(summary = "Change user's avatar")
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

    @PutMapping("/change-bio")
    @Operation(summary = "Change user's bio")
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
