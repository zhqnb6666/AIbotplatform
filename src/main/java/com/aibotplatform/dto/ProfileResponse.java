package com.aibotplatform.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProfileResponse {
    private String username;
    private String email;
    private String role;
    private BigDecimal credits;
    private String avatarUrl;
    private String bio;
}
