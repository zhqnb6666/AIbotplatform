package com.aibotplatform.dto;

import java.util.List;

public record ConversationDTO(
    Long conversationId,
    Long userId,
    Long botId,
    String title
) {
}