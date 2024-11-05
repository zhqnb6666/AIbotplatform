package com.aibotplatform.dto.botDTO;

import com.aibotplatform.model.Bot;
import java.math.BigDecimal;

public record CreateBotRequest(
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
