package com.aibotplatform.controller;
import com.aibotplatform.dto.botDTO.BotResponse;
import com.aibotplatform.dto.profileDTO.ChangeBioRequest;
import com.aibotplatform.dto.profileDTO.ChangeUserNameRequest;
import com.aibotplatform.dto.profileDTO.ProfileResponse;
import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.Bot;
import com.aibotplatform.model.User;
import com.aibotplatform.service.BotService;
import com.aibotplatform.service.FeedbackService;
import com.aibotplatform.service.UserService;
import com.aibotplatform.service.impl.ProfileServiceImpl;
import com.aibotplatform.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
@Tag(name = "Search API", description = "APIS for searching bots or users")
public class SearchController {
    private final BotService botService;
    private final UserServiceImpl userService;
    private final ProfileServiceImpl profileService;

    @GetMapping("/bot/{keyword}")
    @Operation(summary = "Search for bots", description = "Search for bots by name")
    public ResponseEntity<List<BotResponse>> searchBots(@PathVariable String keyword) {
        List<Bot> bots;
        if(keyword.equals("all")){
            bots = botService.getAllBots();
        }else {
            bots = botService.search(keyword);
        }
        List<BotResponse> botResponses = bots.stream().map(this::convertToBotDTO).collect(Collectors.toList());
        return new ResponseEntity<>(botResponses, HttpStatus.OK);
    }

    @GetMapping("/user/{keyword}")
    @Operation(summary = "Search for users", description = "Search for users by username")
    public ResponseEntity<List<ProfileResponse>> searchUsers(@PathVariable String keyword) {
        List<User> users;
        if(keyword.equals("all")){
            users = userService.getAllUsers();
        }else{
            users = userService.search(keyword);
        }
        List<ProfileResponse> profileResponses = users.stream()
                .map(user -> profileService.getUserProfile(user.getUsername()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(profileResponses, HttpStatus.OK);
    }

    private BotResponse convertToBotDTO(Bot bot) {
        return new BotResponse(
                bot.getBotId(),
                bot.getCreator().getUserId(),
                bot.getName(),
                bot.getDescription(),
                bot.getModel(),
                bot.getType(),
                bot.getIsActive(),
                bot.getTokenCost(),
                bot.getPromptTemplate(),
                bot.getGreetingMessage(),
                bot.getTemperature(),
                bot.getAccessibility()
        );
    }
}
