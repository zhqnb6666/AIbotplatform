package com.aibotplatform.dto.botDTO;

public record CreateBotRequest(
        String name,
        String description,
        String model,
        Integer tokenCost
) {
}
