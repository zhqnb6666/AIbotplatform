package com.aibotplatform.dto.botDTO;

import com.aibotplatform.model.Bot;
import org.springframework.web.multipart.MultipartFile;

public record CreateRagBotRequest(
        String name,
        String description,
        String model,
        Integer tokenCost,
        String promptTemplate,
        String greetingMessage,
        Double temperature,
        Bot.BotAccessibility accessibility,
        MultipartFile docFile
) {
}
