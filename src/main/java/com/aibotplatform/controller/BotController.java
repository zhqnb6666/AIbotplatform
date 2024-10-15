package com.aibotplatform.controller;

import com.aibotplatform.dto.BotDTO;
import com.aibotplatform.model.Bot;
import com.aibotplatform.service.BotService;
import com.aibotplatform.service.impl.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bots")
public class BotController {

    private final BotService botService;
    private final UserService userService;

    public BotController(BotService botService,UserService userService) {
        this.botService = botService;
        this.userService = userService;
    }

    // GET /api/bots - Retrieve all bots
    @GetMapping
    public ResponseEntity<List<BotDTO>> getAllBots() {
        List<Bot> bots = botService.getAllBots();
        List<BotDTO> botDTOs = bots.stream().map(this::convertToDTO).collect(Collectors.toList());
        return new ResponseEntity<>(botDTOs, HttpStatus.OK);
    }

    // GET /api/bots/{bot_id} - Get specific bot details
    @GetMapping("/{bot_id}")
    public ResponseEntity<BotDTO> getBotById(@PathVariable Long bot_id) {
        Bot bot = botService.getBotById(bot_id);
        return new ResponseEntity<>(convertToDTO(bot), HttpStatus.OK);
    }

    // POST /api/bots - Create a new custom bot
    @PostMapping
    public ResponseEntity<BotDTO> createBot(@RequestBody @Valid BotDTO botDTO) {
        Bot bot = convertToEntity(botDTO);
        Bot createdBot = botService.createBot(bot, Bot.BotType.CUSTOM);
        return new ResponseEntity<>(convertToDTO(createdBot), HttpStatus.CREATED);
    }

    // PUT /api/bots/{bot_id} - Update a custom bot
    @PutMapping()
    public ResponseEntity<BotDTO> updateBot( @RequestBody @Valid BotDTO botDTO) {
        Bot bot = convertToEntity(botDTO);
        Bot updatedBot = botService.updateBot( bot);
        return new ResponseEntity<>(convertToDTO(updatedBot), HttpStatus.OK);
    }

    // DELETE /api/bots/{bot_id} - Delete a custom bot
    @DeleteMapping("/{bot_id}")
    public ResponseEntity<Void> deleteBot(@PathVariable Long bot_id) {
        botService.deleteBot(bot_id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Convert Bot entity to BotDTO
    private BotDTO convertToDTO(Bot bot) {
        return new BotDTO(
                bot.getBotId(),
                bot.getCreator().getUserId(),
                bot.getName(),
                bot.getDescription(),
                bot.getModel(),
                bot.getActive(),
                bot.getTokenCost()
        );
    }

    // Convert BotDTO to Bot entity
    private Bot convertToEntity(BotDTO botDTO) {
        Bot bot = new Bot();
        bot.setCreator(userService.getUserById(botDTO.userId()));
        bot.setName(botDTO.name());
        bot.setDescription(botDTO.description());
        bot.setModel(botDTO.model());
        bot.setActive(botDTO.isActive());
        bot.setTokenCost(botDTO.tokenCost());
        return bot;
    }
}
