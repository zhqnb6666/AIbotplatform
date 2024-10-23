package com.aibotplatform.service;

import com.aibotplatform.model.User;

import java.math.BigDecimal;

public interface UserService {
    void sendVerificationCode(String email);
    void resetPassword(String email, String verificationCode, String newPassword);
    User registerNewUser(User user, String verificationCode);
    void changeCredits(User user, BigDecimal creditBalance);
    void changeTokens(User user, Long tokenBalance);
    public User getUserById(Long userId);
    public User getUserByName(String username);
}
