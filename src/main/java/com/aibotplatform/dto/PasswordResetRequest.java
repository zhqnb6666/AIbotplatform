package com.aibotplatform.dto;

import lombok.Getter;

@Getter
public class PasswordResetRequest {
    private String email;
    private String verificationCode;
    private String newPassword;
}
