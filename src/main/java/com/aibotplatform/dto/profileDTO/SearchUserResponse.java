package com.aibotplatform.dto.profileDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchUserResponse {
    private Long userId;
    private String username;
    private String email;
    private String role;
    private BigDecimal credits;
    private Long token;
    private String avatarUrl;
    private String bio;

    public SearchUserResponse(Long userId, String username, String email, String string, BigDecimal credits, Long token, String avatarUrl, String bio) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = string;
        this.credits = credits;
        this.token = token;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
    }
}
