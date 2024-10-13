package com.aibotplatform.dto;

public record BotDTO(Long botId,Long userId, String name, String description, String model, Boolean isActive, Integer tokenCost) {
}
