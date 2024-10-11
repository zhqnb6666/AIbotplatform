package com.aibotplatform.dto;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String verificationCode;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getVerificationCode() {
        return verificationCode;
    }
}
