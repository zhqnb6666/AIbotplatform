package com.aibotplatform.service.impl;

import com.aibotplatform.model.Bot;
import com.aibotplatform.repository.BotRepository;
import com.aibotplatform.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class BotServiceImpl implements BotService {

    private final BotRepository botRepository;

    public BotServiceImpl(BotRepository botRepository) {
        this.botRepository = botRepository;
    }

    @Override
    public List<Bot> getAllBots() {
        return botRepository.findAll();
    }

    @Override
    public Bot getBotById(Long botId) {
        return botRepository.findById(botId)
                .orElseThrow(() -> new RuntimeException("Bot not found with id: " + botId));
    }

    @Override
    public Bot createBot(Bot bot,Bot.BotType type) {
        bot.setBotId(null);
        bot.setCreatedAt(Timestamp.from(java.time.Instant.now()));
        bot.setUpdatedAt(Timestamp.from(java.time.Instant.now()));
        bot.setType(type);
        try{
            return botRepository.save(bot);
        } catch (Exception e) {
            throw new RuntimeException("Error creating bot: " + e.getMessage());
        }
    }

    @Override
    public Bot updateBot(Bot bot) {
        Bot existingBot = botRepository.findById(bot.getBotId())
                .orElseThrow(() -> new RuntimeException("Bot not found with id: " + bot.getBotId()));
        existingBot.setName(bot.getName());
        existingBot.setDescription(bot.getDescription());
        existingBot.setModel(bot.getModel());
        existingBot.setActive(bot.getActive());
        existingBot.setTokenCost(bot.getTokenCost());
        existingBot.setUpdatedAt(Timestamp.from(java.time.Instant.now()));
        return botRepository.save(existingBot);
    }

    @Override
    public void deleteBot(Long botId) {
        Bot bot = botRepository.findById(botId)
                .orElseThrow(() -> new RuntimeException("Bot not found with id: " + botId));
        botRepository.delete(bot);
    }

    @Override
    public List<Bot> getUserCustomBots(Long userId) {
        return botRepository.findBotsByCreator_UserId(userId);
    }


}
