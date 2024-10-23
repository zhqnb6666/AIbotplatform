package com.aibotplatform.dto.authDTO;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String usernameOrEmail;
    private String password;
}
