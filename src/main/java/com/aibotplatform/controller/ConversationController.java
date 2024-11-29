package com.aibotplatform.controller;

import com.aibotplatform.dto.ConversationDTO;
import com.aibotplatform.dto.ConversationResponse;
import com.aibotplatform.model.Bot;
import com.aibotplatform.model.Conversation;
import com.aibotplatform.model.Message;
import com.aibotplatform.dto.MessageDTO;
import com.aibotplatform.dto.ErrorResponse;
import com.aibotplatform.model.User;
import com.aibotplatform.service.BotService;
import com.aibotplatform.service.ConversationService;
import com.aibotplatform.service.impl.UserServiceImpl;
import com.aibotplatform.llm.Util;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
@Tag(name = "Conversation API", description = "Manage conversations and messages")
public class ConversationController {

    private final ConversationService conversationService;
    private final UserServiceImpl userService;
    private final BotService botService;
    private final Util util = new Util();

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

    @PostMapping("/{conversationId}/messages")
    @Operation(summary = "Send a message", description = "Send a message to a conversation")
    public ResponseEntity<?> sendMessage(
            @PathVariable Long conversationId,
            @RequestBody String content) {

        // 1. 验证token
        Conversation conversation = conversationService.getConversationById(conversationId);
        int spaceIndex = content.indexOf(' ');
        String botName = content;
        if (spaceIndex != -1) {
            botName = content.substring(0, spaceIndex);
        }
        Bot bot = botName.startsWith("@") ? botService.getBotByName(botName.substring(1)).orElse(conversation.getBot()) : conversation.getBot();
        User currentUser = conversation.getUser();
        int requiredTokens = bot.getTokenCost();
        if (currentUser.getToken() < requiredTokens) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("INSUFFICIENT_TOKENS",
                            "Insufficient tokens. Required: " + requiredTokens +
                                    ", Available: " + currentUser.getToken())
            );
        }

        // 2. 扣减token
        userService.deductTokens(
                currentUser,
                (long) requiredTokens,
                String.format("%s sent a message to %s and consumed %d tokens.",
                        currentUser.getUsername(),
                        bot.getName(),
                        bot.getTokenCost())
        );

        // 3. 保存消息，返回消息ID
        Message message = new Message(conversation,null, Message.SenderType.USER, content);
        Long savedMessageId= conversationService.saveMessage(conversationId, message);
        return ResponseEntity.ok(new ConversationResponse(bot.getBotId(),savedMessageId));
    }

    @GetMapping(value = "/{botId}/messages/{messageId}/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Stream bot responses", description = "Stream real-time responses from the bot in a conversation")
    public Flux<ServerSentEvent<Object>> streamResponse(
            @PathVariable Long botId,
            @PathVariable Long messageId) {

        return conversationService.getMessageStream(botId, messageId)
                .map(content -> ServerSentEvent.builder()
                        .event("message")
                        .data(Base64.getEncoder().encodeToString(content.getBytes()))
                        .build())
                .onErrorResume(error -> Flux.just(
                        ServerSentEvent.builder()
                                .event("error")
                                .data(new ErrorResponse("INTERNAL_ERROR", error.getMessage()))
                                .build()))
                .concatWith(Flux.just(
                        ServerSentEvent.builder()
                                .event("complete")
                                .data("Message completed")
                                .build()));
    }

    @PostMapping("/{conversation_id}/saveResponse")
    @Operation(summary = "Save response", description = "message_id和sendType为非必填项")
    public ResponseEntity<?> saveResponse(@PathVariable Long conversation_id,
            @RequestBody MessageDTO messageDTO) {
        Message message = convertToEntity(messageDTO);
        message.setSenderType(Message.SenderType.BOT);
        conversationService.saveMessage(conversation_id, message);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{conversation_id}")
    @Operation(summary = "Clear conversation context", description = "Delete all messages in a conversation")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long conversation_id) {
        conversationService.deleteConversation(conversation_id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{conversation_id}/chatHistory")
    @Operation(summary = "Get chat history", description = "Retrieve all messages in a conversation")
    public ResponseEntity<List<MessageDTO>> streamMessages(@PathVariable Long conversation_id) {
        List<Message> messages = conversationService.streamMessages(conversation_id);
        List<MessageDTO> messageDTOs = messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(messageDTOs);
    }

    @PostMapping("/predict-next")
    public ResponseEntity<List<String>> predictNextQuestions(@RequestBody String userQuestion) {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonList("Invalid user_question input"));
        }
        System.out.println(userQuestion);
        List<String> predictions = util.predictNextQuestions(userQuestion);
        return ResponseEntity.ok(predictions);
    }

    // 预测标题的 API
    @PostMapping("/predict-title")
    public ResponseEntity<String> predictTitle(@RequestBody String userQuestion) {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid user_question input");
        }
        System.out.println(userQuestion);
        String title = util.predictTittle(userQuestion);
        return ResponseEntity.ok(title);
    }

    private MessageDTO convertToDTO(Message message) {
        return new MessageDTO(message.getMessageId(), message.getSenderType(), message.getBot() != null ? message.getBot().getBotId() : 0, message.getContent());
    }

    private Message convertToEntity(MessageDTO messageDTO) {
        Message message = new Message();
        message.setMessageId(messageDTO.messageId());
        message.setSenderType(messageDTO.senderType());
        if (messageDTO.botId()!=0) {
            message.setBot(botService.getBotById(messageDTO.botId()));
        }
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
