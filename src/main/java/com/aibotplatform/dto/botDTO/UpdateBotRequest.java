package com.aibotplatform.dto.botDTO;

import com.aibotplatform.model.Bot;
import java.math.BigDecimal;

public record UpdateBotRequest(
        Long botId,
        String name,
        String description,
        String model,
        Integer tokenCost,
        String promptTemplate,
        String greetingMessage,
        Double temperature,
        Bot.BotAccessibility accessibility
) {
}
