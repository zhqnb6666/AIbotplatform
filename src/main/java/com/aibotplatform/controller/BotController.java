package com.aibotplatform.controller;

import com.aibotplatform.dto.botDTO.BotResponse;
import com.aibotplatform.dto.botDTO.CreateBotRequest;
import com.aibotplatform.dto.botDTO.UpdateBotRequest;
import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.Bot;
import com.aibotplatform.model.User;
import com.aibotplatform.service.BotService;
import com.aibotplatform.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bots")
@Tag(name = "Bots API", description = "APIs for getting, creating, updating, and deleting bots")
public class BotController {

    private final BotService botService;
    private final UserServiceImpl userService;

    public BotController(BotService botService, UserServiceImpl userService) {
        this.botService = botService;
        this.userService = userService;
    }

    // GET /api/bots - Retrieve all bots
    @GetMapping()
    @Operation(summary = "Retrieve all bots")
    public ResponseEntity<List<BotResponse>> getAllBots() {
        List<Bot> bots = botService.getAllBots();
        List<BotResponse> botResponses = bots.stream().map(this::convertToDTO).collect(Collectors.toList());
        return new ResponseEntity<>(botResponses, HttpStatus.OK);
    }

    // GET /api/bots/{bot_id} - Get specific bot details
    @GetMapping("/{bot_id}")
    @Operation(summary = "Get specific bot details", description = "Get specific bot details by bot id")
    public ResponseEntity<BotResponse> getBotById(@PathVariable Long bot_id) {
        Bot bot = botService.getBotById(bot_id);
        if (bot == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(convertToDTO(bot), HttpStatus.OK);
    }

    // POST /api/bots - Create a new custom bot
    @PostMapping
    @Operation(summary = "Create a new custom bot")
    public ResponseEntity<BotResponse> createBot(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody @Valid CreateBotRequest createBotRequest) {
        User user = userService.getUserByName(userDetails.getUsername());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Bot createdBot = null;
        try {
            createdBot = botService.createBot(createBotRequest, user, Bot.BotType.CUSTOM);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(convertToDTO(createdBot), HttpStatus.CREATED);
    }

    // PUT /api/bots/{bot_id} - Update a custom bot
    @PutMapping()
    @Operation(summary = "Update a custom bot", description = "Update a custom bot, only creator and admin can update")
    public ResponseEntity<BotResponse> updateBot(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody @Valid UpdateBotRequest updateBotRequest) {
        User user = userService.getUserByName(userDetails.getUsername());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Bot updatedBot = null;
        try {
            updatedBot = botService.updateBot(updateBotRequest, user);
        } catch (ApiException e) {
            return new ResponseEntity<>(e.getStatus());
        }
        return new ResponseEntity<>(convertToDTO(updatedBot), HttpStatus.OK);
    }

    // DELETE /api/bots/{bot_id} - Delete a custom bot
    @DeleteMapping("/{bot_id}")
    @Operation(summary = "Delete a custom bot", description = "Delete a custom bot, only creator and admin can delete")
    public ResponseEntity<Void> deleteBot(@AuthenticationPrincipal UserDetails userDetails,
                                          @PathVariable Long bot_id) {
        User user = userService.getUserByName(userDetails.getUsername());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        botService.deleteBot(bot_id, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Convert Bot entity to BotDTO
    private BotResponse convertToDTO(Bot bot) {
        return new BotResponse(
                bot.getBotId(),
                bot.getCreator().getUserId(),
                bot.getName(),
                bot.getDescription(),
                bot.getModel(),
                bot.getType(),
                bot.getIsActive(),
                bot.getTokenCost()
        );
    }
}
