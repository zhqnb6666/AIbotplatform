package com.aibotplatform.service;

import com.aibotplatform.model.Bot;
import com.aibotplatform.model.Conversation;
import com.aibotplatform.model.Message;
import reactor.core.publisher.Flux;

import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;

public interface ConversationService {
    void startConversation(Conversation conversation);
    Conversation getConversationById(Long conversationId);
    List<Conversation> getConversationsByUserId(Long userId);

//    Flux<String> addMessageToConversation(Long conversationId, Message message);

//    Flux<String> chatWithOtherBot(Long conversationId, Message message, Bot bot);

    List<AbstractMap.SimpleEntry<String, String>> getChatHistory(Long conversationId);

    void deleteConversation(Long conversationId);

    List<Message> streamMessages(Long conversationId);

    Message getMessageById(Long messageId);


    Long saveMessage(Long conversationId, Message message);

    Flux<String> getMessageStream(Long botId, Long messageId);
}