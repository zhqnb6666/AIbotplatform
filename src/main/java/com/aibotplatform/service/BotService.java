package com.aibotplatform.service;

import com.aibotplatform.dto.botDTO.CreateBotRequest;
import com.aibotplatform.dto.botDTO.UpdateBotRequest;
import com.aibotplatform.model.Bot;
import com.aibotplatform.model.User;

import java.util.List;
import java.util.Optional;

public interface BotService {
    List<Bot> getAllBots();
    Bot getBotById(Long botId);
    Optional<Bot> getBotByName(String name);
    Bot createBot(CreateBotRequest createBotRequest, User creator, Bot.BotType type);
    Bot updateBot(UpdateBotRequest updateBotRequest, User updateUser);
    void deleteBot(Long botId, User deleteUser);
    List<Bot> getUserCustomBots(Long userId);
}
