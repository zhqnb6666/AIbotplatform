package com.aibotplatform.repository;


import com.aibotplatform.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long>{
    public List<Message> findByConversation_ConversationId(Long conversationId);

    Optional<Message> findByMessageId(Long messageId);
}
