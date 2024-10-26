package com.aibotplatform.service.impl;

import com.aibotplatform.dto.botDTO.CreateBotRequest;
import com.aibotplatform.dto.botDTO.UpdateBotRequest;
import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.Bot;
import com.aibotplatform.model.User;
import com.aibotplatform.repository.BotRepository;
import com.aibotplatform.service.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BotServiceImpl implements BotService {

    private final BotRepository botRepository;
    private final UserServiceImpl userService;

    @Override
    public List<Bot> getAllBots() {
        return botRepository.findByIsActiveTrue();
    }

    @Override
    public Bot getBotById(Long botId) {
        return botRepository.findById(botId)
                .orElseThrow(() -> new ApiException("Bot Not Found", HttpStatus.NOT_FOUND));
    }

    @Override
    public Optional<Bot> getBotByName(String name) {
        return botRepository.findByName(name);
    }

    @Override
    public Bot createBot(CreateBotRequest createBotRequest, User creator, Bot.BotType type) {
        Bot bot = new Bot();
        bot.setName(createBotRequest.name());
        bot.setCreator(creator);
        bot.setDescription(createBotRequest.description());
        bot.setModel(createBotRequest.model());
        bot.setTokenCost(createBotRequest.tokenCost());
        bot.setIsActive(true);
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
    public Bot updateBot(UpdateBotRequest updateBotRequest, User updateUser) throws ApiException {
        Bot existingBot = getBotById(updateBotRequest.botId());
        if (existingBot == null) {
            throw new ApiException("Bot Not Found",HttpStatus.NOT_FOUND);
        }
        if (updateUser.getRole().equals(User.Role.USER) && !existingBot.getCreator().getUserId().equals(updateUser.getUserId())) {
            throw new ApiException("You are not authorized to update this bot", HttpStatus.UNAUTHORIZED);
        }
        if (!existingBot.getIsActive()) {
            throw new ApiException("Bot is deleted", HttpStatus.BAD_REQUEST);
        }
        if (updateBotRequest.description() != null)
            existingBot.setDescription(updateBotRequest.description());
        if (updateBotRequest.tokenCost() != null)
            existingBot.setTokenCost(updateBotRequest.tokenCost());
        if (updateBotRequest.model() != null)
            existingBot.setModel(updateBotRequest.model());
        if (updateBotRequest.name() != null)
            existingBot.setName(updateBotRequest.name());
        existingBot.setUpdatedAt(Timestamp.from(java.time.Instant.now()));
        return botRepository.save(existingBot);
    }

    @Override
    public void deleteBot(Long botId, User deleteUser) throws ApiException{
        Bot bot = getBotById(botId);
        if (bot == null) {
            throw new ApiException("Bot Not Found",HttpStatus.NOT_FOUND);
        }
        if (deleteUser.getRole().equals(User.Role.USER) && !bot.getCreator().getUserId().equals(deleteUser.getUserId())) {
            throw new ApiException("You are not authorized to delete this bot", HttpStatus.UNAUTHORIZED);
        }
        if (!bot.getIsActive()) {
            throw new ApiException("Bot is deleted", HttpStatus.BAD_REQUEST);
        }
        bot.setIsActive(false);
        botRepository.save(bot);
    }

    @Override
    public List<Bot> getUserCustomBots(Long userId) {
        try {
            List<Bot> userBotsList = botRepository.findBotsByCreator_UserId(userId);
            for (Bot bot : userBotsList) {
                if (!bot.getIsActive()) {
                    userBotsList.remove(bot);
                } else if (!bot.getType().equals(Bot.BotType.CUSTOM)) {
                    userBotsList.remove(bot);
                }
            }
            return userBotsList;
        } catch (Exception e) {
            throw new ApiException("User Not Found",HttpStatus.NOT_FOUND);
        }
    }


}
