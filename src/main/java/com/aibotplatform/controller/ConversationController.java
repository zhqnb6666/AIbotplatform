package com.aibotplatform.controller;

import com.aibotplatform.dto.ConversationDTO;
import com.aibotplatform.model.Bot;
import com.aibotplatform.model.Conversation;
import com.aibotplatform.model.Message;
import com.aibotplatform.dto.MessageDTO;
import com.aibotplatform.dto.ErrorResponse;
import com.aibotplatform.model.User;
import com.aibotplatform.service.BotService;
import com.aibotplatform.service.ConversationService;
import com.aibotplatform.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
@Tag(name = "Conversation API", description = "Manage conversations and messages")
public class ConversationController {

    private final ConversationService conversationService;
    private final UserServiceImpl userService;
    private final BotService botService;

    public ConversationController(ConversationService conversationService, UserServiceImpl userService, BotService botService) {
        this.conversationService = conversationService;
        this.botService = botService;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Start a new conversation", description = "Create a new conversation for a user")
    public ResponseEntity<ConversationDTO> startConversation(@AuthenticationPrincipal UserDetails userDetails,
                                                             @RequestBody ConversationDTO conversation) {
        Conversation startedConversation = convertToEntity(conversation);
        User user = userService.getUserByName(userDetails.getUsername());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        startedConversation.setUser(user);
        conversationService.startConversation(startedConversation);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(startedConversation));
    }

    @GetMapping()
    @Operation(summary = "Get conversations of user",description = "Retrieve all active conversation created by user")
    public ResponseEntity<List<ConversationDTO>> getConversations(@AuthenticationPrincipal UserDetails userDetails){
        User user = userService.getUserByName(userDetails.getUsername());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        List<Conversation> conversations = conversationService.getConversationsByUserId(user.getUserId());
        List<ConversationDTO> conversationDTOS = conversations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(conversationDTOS);
    }
    @GetMapping("/{conversation_id}")
    @Operation(summary = "Retrieve a conversation", description = "Get details of a specific conversation")
    public ResponseEntity<Conversation> getConversation(@PathVariable Long conversation_id) {
        Conversation conversation = conversationService.getConversationById(conversation_id);
        return ResponseEntity.ok(conversation);
    }

    @PostMapping("/{conversation_id}/messages")
    @Operation(summary = "Send a message in a conversation", description = "Send a new message in a specific conversation")
    public ResponseEntity<?> sendMessage(@PathVariable Long conversation_id, @RequestBody MessageDTO messageDTO) {
        try {
            // 1. 获取conversation对应的bot和token cost
            Conversation conversation = conversationService.getConversationById(conversation_id);
            Bot bot = conversation.getBot();
            User currentUser = conversation.getUser();
            int requiredTokens = bot.getTokenCost();

            if (currentUser.getToken() < requiredTokens) {
                return ResponseEntity
                        .status(HttpStatus.PAYMENT_REQUIRED)
                        .body(new ErrorResponse(
                                "INSUFFICIENT_TOKENS",
                                "Insufficient tokens to send message. Required: " + requiredTokens +
                                        ", Available: " + currentUser.getToken()
                        ));
            }

            Message message = convertToEntity(messageDTO);
            Message response = conversationService.addMessageToConversation(conversation_id, message);
            userService.deductTokens(currentUser, (long) requiredTokens,String.format("%s sent a message to %s and consumed %d tokens.",currentUser.getUsername(), bot.getName(),bot.getTokenCost()));
            MessageDTO responseDTO = convertToDTO(response);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "Error" + e));
        }
    }

    @DeleteMapping("/{conversation_id}")
    @Operation(summary = "Clear conversation context", description = "Delete all messages in a conversation")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long conversation_id) {
        conversationService.deleteConversation(conversation_id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{conversation_id}/stream")
    @Operation(summary = "Stream bot responses", description = "Stream real-time responses from the bot in a conversation")
    public ResponseEntity<List<MessageDTO>> streamMessages(@PathVariable Long conversation_id) {
        List<Message> messages = conversationService.streamMessages(conversation_id);
        List<MessageDTO> messageDTOs = messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(messageDTOs);
    }

    private MessageDTO convertToDTO(Message message) {
        return new MessageDTO(message.getMessageId(), message.getSenderType(), message.getContent());
    }

    private Message convertToEntity(MessageDTO messageDTO) {
        Message message = new Message();
        message.setMessageId(messageDTO.messageId());
        message.setSenderType(messageDTO.senderType());
        message.setContent(messageDTO.content());
        return message;
    }

private  ConversationDTO convertToDTO(Conversation conversation) {
        return new ConversationDTO(
                conversation.getConversationId(),
                conversation.getBot().getBotId(),
                conversation.getTitle()
        );
    }
    private Conversation convertToEntity(ConversationDTO conversationDTO) {
        Conversation conversation = new Conversation();
        conversation.setConversationId(conversationDTO.conversationId());
        conversation.setBot(botService.getBotById(conversationDTO.botId()));
        conversation.setTitle(conversationDTO.title());
        return conversation;
    }



}
