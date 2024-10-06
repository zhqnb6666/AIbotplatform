package com.aibotplatform.service;

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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationService verificationService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPasswordHash(), new ArrayList<>());
    }

//    public User registerNewUser(User user) {
//        // Hash the password before saving
//        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
//
//        // Set default values for credits, createdAt, and updatedAt
//        Timestamp currentTimestamp = Timestamp.from(Instant.now());
//        user.setCreatedAt(currentTimestamp);  // Set current time as createdAt
//        user.setUpdatedAt(currentTimestamp);  // Set current time as updatedAt
//
//        // Set the default role if not already set (it defaults to USER)
//        if (user.getRole() == null) {
//            user.setRole(User.Role.USER);
//        }
//
//        return userRepository.save(user);
//    }

//    public void sendVerificationCode(String email) {
//        String code = verificationService.generateVerificationCode(email);
//        emailService.sendVerificationEmail(email, code);
//    }

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
            userRepository.save(user);
            verificationService.clearCode(email);
        } else {
            throw new IllegalArgumentException("Invalid verification code");
        }
    }

    public User registerNewUser(User user, String verificationCode) {
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
}