package com.aibotplatform.dto.profileDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;


@Data
@AllArgsConstructor
public class UserFeedbackResponse {
    private String commenter;
    private String commenterAvatarUrl;
    private Integer rating;
    private String content;
    private Timestamp createdAt;
}
