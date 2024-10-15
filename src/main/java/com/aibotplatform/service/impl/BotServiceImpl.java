package com.aibotplatform.service.impl;

import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.Bot;
import com.aibotplatform.repository.BotRepository;
import com.aibotplatform.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
                .orElseThrow(() -> new ApiException("Bot Not Found", HttpStatus.NOT_FOUND));
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
        Bot existingBot = getBotById(bot.getBotId());
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
        Bot bot = getBotById(botId);
        botRepository.delete(bot);
    }

    @Override
    public List<Bot> getUserCustomBots(Long userId) {
        try {
            return botRepository.findBotsByCreator_UserId(userId);
        } catch (Exception e) {
            throw new ApiException("User Not Found",HttpStatus.NOT_FOUND);
        }
    }


}
