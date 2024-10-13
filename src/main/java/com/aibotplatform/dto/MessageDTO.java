package com.aibotplatform.dto;

import com.aibotplatform.model.Message;

public record MessageDTO(
    Long messageId,
    Message.SenderType senderType,
    String content

) {
}