package com.aibotplatform.dto;

import java.util.List;

public record ConversationDTO(
    Long conversationId,
    Long botId,
    String title
) {
}