package com.aibotplatform.dto.profileDTO;

import com.aibotplatform.model.Bot;
import com.aibotplatform.model.UserFeedback;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProfileResponse {

    private String username;
    private String email;
    private String role;
    private BigDecimal credits;
    private String avatarUrl;
    private String bio;
    private List<Bot> userBotList;
    private List<UserFeedback> userFeedbackList;
}
