package com.aibotplatform.dto.botDTO;

public record UpdateBotRequest(
        Long botId,
        String name,
        String description,
        String model,
        Integer tokenCost
) {
}
