package com.aibotplatform.service;

public interface VerificationService {
    String generateVerificationCode(String email);
    boolean canResendCode(String email);
    boolean verifyCode(String email, String code);
    void clearCode(String email);
}
