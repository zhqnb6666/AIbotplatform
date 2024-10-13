package com.aibotplatform.service;

import com.aibotplatform.model.Conversation;
import com.aibotplatform.model.Message;

import java.util.List;

public interface ConversationService {
    void startConversation(Conversation conversation);
    Conversation getConversationById(Long conversationId);
    List<Conversation> getConversationsByUserId(Long userId);

    void addMessageToConversation(Long conversationId, Message message);

    void deleteConversation(Long conversationId);

    List<Message> streamMessages(Long conversationId);




}