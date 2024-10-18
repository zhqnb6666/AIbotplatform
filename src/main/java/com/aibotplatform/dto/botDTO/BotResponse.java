package com.aibotplatform.dto.botDTO;

public record BotResponse(
        Long botId,
        Long userId,
        String name,
        String description,
        String model,
        Boolean isActive,
        Integer tokenCost
) {
}
