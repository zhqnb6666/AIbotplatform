package com.aibotplatform.dto.botDTO;

import com.aibotplatform.model.Bot;

public record BotResponse(
        Long botId,
        Long userId,
        String name,
        String description,
        String model,
        Bot.BotType type,
        Boolean isActive,
        Integer tokenCost
) {
}
