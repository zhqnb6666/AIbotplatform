package com.aibotplatform.service;

import com.aibotplatform.model.Bot;
import com.aibotplatform.model.User;

import java.util.List;

public interface BotService {
    List<Bot> getAllBots();
    Bot getBotById(Long botId);
    Bot createBot(Bot bot, Bot.BotType type);
    Bot updateBot( Bot bot);
    void deleteBot(Long botId);

    List<Bot> getUserCustomBots(Long userId);
}
