package com.aibotplatform.dto.authDTO;

import lombok.Getter;

@Getter
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String verificationCode;
}
