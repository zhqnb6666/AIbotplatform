package com.aibotplatform.service.impl;

import com.aibotplatform.dto.UserStatsResponse;
import com.aibotplatform.exception.ApiException;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.aibotplatform.model.User;
import com.aibotplatform.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final VerificationService verificationService;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, VerificationService verificationService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordHash(), new ArrayList<>());
    }


    public void sendVerificationCode(String email) {
        if (verificationService.canResendCode(email)) {
            String code = verificationService.generateVerificationCode(email);
            emailService.sendVerificationEmail(email, code);
        } else {
            throw new RuntimeException("Please wait before requesting a new code");
        }
    }

    public void resetPassword(String email, String verificationCode, String newPassword) {
        if (verificationService.verifyCode(email, verificationCode)) {
            User user = userRepository.findByEmail(email);
            if(user == null) {
                throw new IllegalArgumentException("User not found");
            }
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(Timestamp.from(Instant.now()));
            userRepository.save(user);
            verificationService.clearCode(email);
        } else {
            throw new IllegalArgumentException("Invalid verification code");
        }
    }

    public User registerNewUser(User user, String verificationCode) {
        if(userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        if(userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (!verificationService.verifyCode(user.getEmail(), verificationCode)) {
            throw new IllegalArgumentException("Invalid verification code");
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        //        // Set default values for credits, createdAt, and updatedAt
        Timestamp currentTimestamp = Timestamp.from(Instant.now());
        user.setCreatedAt(currentTimestamp);  // Set current time as createdAt
        user.setUpdatedAt(currentTimestamp);  // Set current time as updatedAt
        // Set the default role if not already set (it defaults to USER)
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }
        User savedUser = userRepository.save(user);
        verificationService.clearCode(user.getEmail());
        return savedUser;
    }

    public User updateUserProfile(Long userId, User user) {
    User existingUser = userRepository.findByUserId(userId);
    if (existingUser == null) {
        throw new IllegalArgumentException("userId not found");
    }
    existingUser.setUsername(user.getUsername());
    existingUser.setEmail(user.getEmail());
    existingUser.setRole(user.getRole());
    existingUser.setCredits(user.getCredits());
    return userRepository.save(existingUser);
}

//    public UserStatsResponse getUserStats(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
//        return new UserStatsResponse();
//    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User NOT Found", HttpStatus.NOT_FOUND));
    }
}