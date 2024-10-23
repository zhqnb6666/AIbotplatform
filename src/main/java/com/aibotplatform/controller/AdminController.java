package com.aibotplatform.controller;

import com.aibotplatform.dto.botDTO.BotResponse;
import com.aibotplatform.dto.botDTO.CreateBotRequest;
import com.aibotplatform.dto.botDTO.UpdateBotRequest;
import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.Bot;
import com.aibotplatform.model.User;
import com.aibotplatform.service.BotService;
import com.aibotplatform.service.impl.AdminServiceImpl;
import com.aibotplatform.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin API", description = "APIs for admin management")
public class AdminController {
    private final BotService botService;
    private final UserServiceImpl userService;
    private final AdminServiceImpl adminService;

    @PostMapping("/bot")
    @Operation(summary = "Create a new official bot", description = "Create a new official bot, only admin can create")
    public ResponseEntity<?> createOfficialBot(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CreateBotRequest createBotRequest) {
        User user = userService.getUserByName(userDetails.getUsername());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!user.getRole().equals(User.Role.ADMIN)) {
            return new ResponseEntity<>("Only admin can create official bot.", HttpStatus.FORBIDDEN);
        }
        Bot createdBot = null;
        try {
            createdBot = botService.createBot(createBotRequest, user, Bot.BotType.OFFICIAL);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(convertToDTO(createdBot), HttpStatus.CREATED);
    }

    @PutMapping("/bot")
    @Operation(summary = "Update an official bot", description = "Update an official bot, only admin can update")
    public ResponseEntity<?> updateBot(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid UpdateBotRequest updateBotRequest) {
        User user = userService.getUserByName(userDetails.getUsername());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!user.getRole().equals(User.Role.ADMIN)) {
            return new ResponseEntity<>("Only admin can update official bot.", HttpStatus.FORBIDDEN);
        }
        Bot updatedBot = null;
        try {
            updatedBot = botService.updateBot(updateBotRequest, user);
        } catch (ApiException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }
        return new ResponseEntity<>(convertToDTO(updatedBot), HttpStatus.OK);
    }

    @DeleteMapping("/bot/{bot_id}")
    @Operation(summary = "Delete an official bot", description = "Delete an official bot, only admin can delete")
    public ResponseEntity<?> deleteBot(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long bot_id) {
        User user = userService.getUserByName(userDetails.getUsername());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!user.getRole().equals(User.Role.ADMIN)) {
            return new ResponseEntity<>("Only admin can delete official bot.", HttpStatus.FORBIDDEN);
        }
        botService.deleteBot(bot_id, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/export")
    @Operation(summary = "Export info to excel", description = "Export info to excel, only admin can export")
    public ResponseEntity<?> exportInfoExcel(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getUserByName(userDetails.getUsername());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!user.getRole().equals(User.Role.ADMIN)) {
            return new ResponseEntity<>("Only admin can export info.", HttpStatus.FORBIDDEN);
        }

        String fileName = null;
        try {
            fileName = adminService.exportInfo();
        } catch (ApiException e) {
            return new ResponseEntity<>(e.getMessage(), e.getStatus());
        }

        return new ResponseEntity<>("Successfully export platform information into " + fileName , HttpStatus.OK);
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
