package com.aibotplatform.dto;

public class LoginRequest {
    private String usernameOrEmail;
    private String password;

    public String getUsername() {
        return usernameOrEmail;
    }



    public String getPassword() {
        return password;
    }


}
