package com.aibotplatform.service.impl;

import com.aibotplatform.dto.botDTO.CreateBotRequest;
import com.aibotplatform.dto.botDTO.CreateRagBotRequest;
import com.aibotplatform.dto.botDTO.UpdateBotRequest;
import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.Bot;
import com.aibotplatform.model.User;
import com.aibotplatform.repository.BotRepository;
import com.aibotplatform.service.BotService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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
        Bot bot = getBot(createBotRequest, creator, type);
        try {
            return botRepository.save(bot);
        } catch (Exception e) {
            throw new RuntimeException("Error creating bot: " + e.getMessage());
        }
    }

    @NotNull
    private static Bot getBot(CreateBotRequest createBotRequest, User creator, Bot.BotType type) {
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
        bot.setPromptTemplate(createBotRequest.promptTemplate());
        bot.setGreetingMessage(createBotRequest.greetingMessage());
        bot.setTemperature(createBotRequest.temperature());
        bot.setAccessibility(createBotRequest.accessibility());
        return bot;
    }

    @NotNull
    private static Bot getBot(CreateRagBotRequest createBotRequest, User creator, Bot.BotType type) {
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
        bot.setPromptTemplate(createBotRequest.promptTemplate());
        bot.setGreetingMessage(createBotRequest.greetingMessage());
        bot.setTemperature(createBotRequest.temperature());
        bot.setAccessibility(createBotRequest.accessibility());
        return bot;
    }

    @Override
    public Bot createBotWithRag(CreateRagBotRequest createRagBotRequest, User creator, Bot.BotType type, String ragDocUrl) {
        Bot bot = getBot(createRagBotRequest, creator, type);
        bot.setRagDocUrl(ragDocUrl);
        try {
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
        if (updateBotRequest.promptTemplate() != null)
            existingBot.setPromptTemplate(updateBotRequest.promptTemplate());
        if (updateBotRequest.greetingMessage() != null)
            existingBot.setGreetingMessage(updateBotRequest.greetingMessage());
        if (updateBotRequest.temperature() != null)
            existingBot.setTemperature(updateBotRequest.temperature());
        if (updateBotRequest.accessibility() != null)
            existingBot.setAccessibility(updateBotRequest.accessibility());
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
            return botRepository.findBotsByCreator_UserIdAndIsActiveTrue(userId);
        } catch (Exception e) {
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Bot> getLatestBots(Integer top) {
        try {
            List<Bot> topBots = botRepository.findLatestBots(top);
            return topBots;
        } catch (Exception e) {
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Bot> getMostPopularBots(Integer top) {
        try {
            List<Bot> topBots = botRepository.findMostPopularBots(top);
            return topBots;
        } catch (Exception e) {
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Bot> getHistoricalBestBots(Integer top) {
        try {
            List<Bot> topBots = botRepository.findHistoricalBestBots(top);
            return topBots;
        } catch (Exception e) {
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Bot> getMonthlyBestBots(Integer top) {
        try {
            LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
            List<Bot> topBots = botRepository.findMonthlyBestBots(oneMonthAgo, top);
            return topBots;
        } catch (Exception e) {
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Bot> search(String keyword) {
        return botRepository.findBotsByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }


    @Override
    public List<Bot> getRecommendedBots(String username) throws ApiException {
        User user = userService.getUserByName(username);
        if (user == null) {
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        }
        return botRepository.findRecommendedBotsByUserPreferences(user.getUserId());
    }

    @Override
    public String saveRagDoc(String ragDocPath, MultipartFile docFile) throws ApiException {
        String newFileName = UUID.randomUUID().toString() + ".pdf";
        try {
            Path path = Paths.get(ragDocPath, newFileName);
            Files.createDirectories(path.getParent()); // 确保目录存在
            docFile.transferTo(path);
        } catch (IOException e) {
            throw new ApiException("Error saving file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return newFileName;
    }

    @Override
    public Bot updateRag(Long botId, String ragUrl, User updateUser) {
        Bot existingBot = getBotById(botId);
        if (existingBot == null) {
            throw new ApiException("Bot Not Found",HttpStatus.NOT_FOUND);
        }
        if (updateUser.getRole().equals(User.Role.USER) && !existingBot.getCreator().getUserId().equals(updateUser.getUserId())) {
            throw new ApiException("You are not authorized to update this bot", HttpStatus.UNAUTHORIZED);
        }
        if (!existingBot.getIsActive()) {
            throw new ApiException("Bot is deleted", HttpStatus.BAD_REQUEST);
        }
        existingBot.setRagDocUrl(ragUrl);
        existingBot.setUpdatedAt(Timestamp.from(java.time.Instant.now()));
        return botRepository.save(existingBot);
    }
}
