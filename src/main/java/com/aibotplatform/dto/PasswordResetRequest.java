package com.aibotplatform.dto;

public class PasswordResetRequest {
    private String email;
    private String verificationCode;
    private String newPassword;

    public String getEmail() {
        return email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
