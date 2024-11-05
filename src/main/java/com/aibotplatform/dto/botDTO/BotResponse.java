package com.aibotplatform.dto.botDTO;

import com.aibotplatform.model.Bot;

import java.math.BigDecimal;

public record BotResponse(
        Long botId,
        Long userId,
        String name,
        String description,
        String model,
        Bot.BotType type,
        Boolean isActive,
        Integer tokenCost,
        String promptTemplate,
        String greetingMessage,
        Double temperature,
        Bot.BotAccessibility accessibility
) {
}
