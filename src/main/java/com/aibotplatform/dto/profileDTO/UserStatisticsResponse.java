package com.aibotplatform.dto.profileDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatisticsResponse {
    private Long botCount;
    private Long conversationCount;
    private Long tokenConsumed;
    private Long userCommentCount;
    private Long botCommentCount;
}
