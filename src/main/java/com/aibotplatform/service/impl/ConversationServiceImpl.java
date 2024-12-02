package com.aibotplatform.service.impl;

import com.aibotplatform.exception.ApiException;
import com.aibotplatform.llm.LLMSessionManager;
import com.aibotplatform.model.Bot;
import com.aibotplatform.model.Conversation;
import com.aibotplatform.model.Message;
import com.aibotplatform.repository.BotRepository;
import com.aibotplatform.repository.ConversationRepository;
import com.aibotplatform.repository.MessageRepository;
import com.aibotplatform.service.BotService;
import com.aibotplatform.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final LLMSessionManager llmSessionManager;
    private final BotService botService;

    @Autowired
    public ConversationServiceImpl(ConversationRepository conversationRepository, MessageRepository messageRepository, LLMSessionManager llmSessionManager, BotService botService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.llmSessionManager = llmSessionManager;
        this.botService = botService;
    }

    @Transactional
    public void startConversation(Conversation conversation) {
        conversation.setConversationId(null);
        conversation.setActive(true);
        conversation.setCreatedAt(Timestamp.from(Instant.now()));
        conversation.setUpdatedAt(Timestamp.from(Instant.now()));
        conversationRepository.save(conversation);
    }

    public Conversation getConversationById(Long conversationId) {
        return conversationRepository.findByConversationIdAndActiveTrue(conversationId)
                .orElseThrow(() -> new ApiException("Conversation Not Found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Long saveMessage(Long conversationId, Message message) {
        Conversation conversation = getConversationById(conversationId);
        message.setConversation(conversation);
        message.setMessageId(null);
        message.setCreatedAt(Timestamp.from(Instant.now()));
        return messageRepository.save(message).getMessageId();
    }



    @Transactional
    @Override
    public Flux<String> getMessageStream(Long botId, Long messageId, Boolean isSingleTurn) {
        Message message = getMessageById(messageId);
        Conversation conversation = message.getConversation();
        Bot bot = botService.getBotById(botId);
        return llmSessionManager.chat(bot, message.getContent(), isSingleTurn ? null : getChatHistory(conversation.getConversationId()));
    }

//    @Transactional
//    public Flux<String> chatWithOtherBot(Long conversationId, Message message, Bot bot){
//        Conversation conversation = getConversationById(conversationId);
//        message.setConversation(conversation);
//        message.setMessageId(null);
//        message.setCreatedAt(Timestamp.from(Instant.now()));
//        messageRepository.save(message);
//        return(llmSessionManager.chat(bot.getModel(), message.getContent(), getChatHistory(conversationId)));
//    }

    @Transactional
    public void deleteConversation(Long conversationId) {
        Conversation conversation = getConversationById(conversationId);
        conversation.setActive(false);
        conversation.setUpdatedAt(Timestamp.from(Instant.now()));
        conversationRepository.save(conversation);
    }

    public List<Message> streamMessages(Long conversationId) {
        Conversation conversation = getConversationById(conversationId);
        List<Message> messages = messageRepository.findByConversation_ConversationId(conversation.getConversationId());
        messages.sort((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()));
        return messages;
    }

    public List<AbstractMap.SimpleEntry<String, String>> getChatHistory(Long conversationId) {
        List<Message> messages = streamMessages(conversationId);
        List<AbstractMap.SimpleEntry<String, String>> chatHistory = new ArrayList<>();
        for (Message message : messages) {
            String sender = message.getSenderType().name().equals("USER") ? "user" : "assistant";
            String content = message.getContent();
            chatHistory.add(new AbstractMap.SimpleEntry<>(sender, content));
        }
        return chatHistory;
    }

    public List<Conversation> getConversationsByUserId(Long userId) {
        return conversationRepository.findByUser_UserIdAndActiveTrue(userId);
    }

    @Override
    public void changeConversationTitle(Long conversationId, String title) {
        Conversation conversation = getConversationById(conversationId);
        conversation.setTitle(title);
        conversationRepository.save(conversation);
    }

    public Message getMessageById(Long messageId) {
        return messageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new ApiException("Message Not Found", HttpStatus.NOT_FOUND));
    }


}
