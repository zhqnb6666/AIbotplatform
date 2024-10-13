package com.aibotplatform.controller;

import com.aibotplatform.dto.ConversationDTO;
import com.aibotplatform.model.Conversation;
import com.aibotplatform.model.Message;
import com.aibotplatform.dto.MessageDTO;
import com.aibotplatform.service.BotService;
import com.aibotplatform.service.ConversationService;
import com.aibotplatform.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
@Tag(name = "Conversation API", description = "Manage conversations and messages")
public class ConversationController {

    private final ConversationService conversationService;
    private final UserService userService;
    private final BotService botService;

    public ConversationController(ConversationService conversationService, UserService userService, BotService botService) {
        this.conversationService = conversationService;
        this.botService = botService;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Start a new conversation", description = "Create a new conversation for a user")
    public ResponseEntity<ConversationDTO> startConversation(@RequestBody ConversationDTO conversation) {
        Conversation startedConversation = convertToEntity(conversation);
        conversationService.startConversation(startedConversation);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(startedConversation));
    }

    @GetMapping("/{conversation_id}")
    @Operation(summary = "Retrieve a conversation", description = "Get details of a specific conversation")
    public ResponseEntity<Conversation> getConversation(@PathVariable Long conversation_id) {
        Conversation conversation = conversationService.getConversationById(conversation_id);
        return ResponseEntity.ok(conversation);
    }

    @PostMapping("/{conversation_id}/messages")
    @Operation(summary = "Send a message in a conversation", description = "Send a new message in a specific conversation")
    public ResponseEntity<Void> sendMessage(@PathVariable Long conversation_id, @RequestBody MessageDTO messageDTO) {
        Message message = convertToEntity(messageDTO);
        conversationService.addMessageToConversation(conversation_id, message);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
                conversation.getUser().getUserId(),
                conversation.getBot().getBotId(),
                conversation.getTitle()
        );
    }
    private Conversation convertToEntity(ConversationDTO conversationDTO) {
        Conversation conversation = new Conversation();
        conversation.setConversationId(conversationDTO.conversationId());
        conversation.setUser(userService.getUserById(conversationDTO.userId()));
        conversation.setBot(botService.getBotById(conversationDTO.botId()));
        conversation.setTitle(conversationDTO.title());
        return conversation;
    }



}
