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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @PostMapping(value = "/{conversation_id}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Send a message in a conversation", description = "Send a new message in a specific conversation with streaming response")
    public Flux<ServerSentEvent<Object>> sendMessage(
            @PathVariable Long conversation_id,
            @RequestBody MessageDTO messageDTO) {

        return Flux.create(emitter -> {
            try {
                Conversation conversation = conversationService.getConversationById(conversation_id);
                Bot bot = conversation.getBot();
                User currentUser = conversation.getUser();
                int requiredTokens = bot.getTokenCost();
                if (currentUser.getToken() < requiredTokens) {
                    emitter.next(ServerSentEvent.builder()
                            .event("error")
                            .data(new ErrorResponse(
                                    "INSUFFICIENT_TOKENS",
                                    "Insufficient tokens to send message. Required: " + requiredTokens +
                                            ", Available: " + currentUser.getToken()
                            ))
                            .build());
                    emitter.complete();
                    return;
                }

                Message message = convertToEntity(messageDTO);
                Flux<String> responseFlux = conversationService.addMessageToConversation(conversation_id, message);

                userService.deductTokens(
                        currentUser,
                        (long) requiredTokens,
                        String.format("%s sent a message to %s and consumed %d tokens.",
                                currentUser.getUsername(),
                                bot.getName(),
                                bot.getTokenCost())
                );

                responseFlux.subscribe(
                        content -> {
                            emitter.next(ServerSentEvent.builder()
                                    .event("message")
                                    .data(content)
                                    .build());
                        },
                        error -> {
                            emitter.next(ServerSentEvent.builder()
                                    .event("error")
                                    .data(new ErrorResponse("INTERNAL_ERROR", error.getMessage()))
                                    .build());
                            emitter.complete();
                        },
                        () -> {
                            emitter.next(ServerSentEvent.builder()
                                    .event("complete")
                                    .data("Message completed")
                                    .build());
                            emitter.complete();
                        }
                );

            } catch (Exception e) {
                emitter.next(ServerSentEvent.builder()
                        .event("error")
                        .data(new ErrorResponse("INTERNAL_ERROR", "Error: " + e.getMessage()))
                        .build());
                emitter.complete();
            }
        });
    }

    @PostMapping(value = "/{conversation_id}/{bot_id}/messages",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Send a message in a conversation", description = "Send a new message in a specific conversation")
    public Flux<ServerSentEvent<Object>> sendMessageWithOtherBot(
            @PathVariable Long conversation_id,
            @PathVariable Long bot_id,
            @RequestBody MessageDTO messageDTO) {
        return Flux.create(emitter -> {
            try {
                // 1. 验证会话和用户token（保持同步操作）
                Conversation conversation = conversationService.getConversationById(conversation_id);
                Bot bot = botService.getBotById(bot_id);
                User currentUser = conversation.getUser();
                int requiredTokens = bot.getTokenCost();

                if (currentUser.getToken() < requiredTokens) {
                    emitter.next(ServerSentEvent.builder()
                            .event("error")
                            .data(new ErrorResponse(
                                    "INSUFFICIENT_TOKENS",
                                    "Insufficient tokens to send message. Required: " + requiredTokens +
                                            ", Available: " + currentUser.getToken()
                            ))
                            .build());
                    emitter.complete();
                    return;
                }

                // 2. 转换消息并获取响应流
                Message message = convertToEntity(messageDTO);
                Flux<String> responseFlux = conversationService.chatWithOtherBot(conversation_id,message,bot);

                // 3. 扣除token（异步执行）
                userService.deductTokens(
                        currentUser,
                        (long) requiredTokens,
                        String.format("%s sent a message to %s and consumed %d tokens.",
                                currentUser.getUsername(),
                                bot.getName(),
                                bot.getTokenCost())
                );

                // 4. 订阅响应流并发送事件
                responseFlux.subscribe(
                        content -> {
                            emitter.next(ServerSentEvent.builder()
                                    .event("message")
                                    .data(content)
                                    .build());
                        },
                        error -> {
                            emitter.next(ServerSentEvent.builder()
                                    .event("error")
                                    .data(new ErrorResponse("INTERNAL_ERROR", error.getMessage()))
                                    .build());
                            emitter.complete();
                        },
                        () -> {
                            emitter.next(ServerSentEvent.builder()
                                    .event("complete")
                                    .data("Message completed")
                                    .build());
                            emitter.complete();
                        }
                );

            } catch (Exception e) {
                emitter.next(ServerSentEvent.builder()
                        .event("error")
                        .data(new ErrorResponse("INTERNAL_ERROR", "Error: " + e.getMessage()))
                        .build());
                emitter.complete();
            }
        });
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
