package com.aibotplatform.repository;


import com.aibotplatform.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation,Long> {
    public Conversation findByConversationId(Long conversationId);
    public List<Conversation> findByUser_UserIdAndActiveTrue(Long userId);

    public Optional<Conversation> findByConversationIdAndActiveTrue(Long conversationId);

    @Query("SELECT COUNT(DISTINCT c.bot) " +
            "FROM Conversation c " +
            "WHERE c.user.userId = :userId AND c.active = true")
    Long countConversationBotByUserId(Long userId);

    @Query("SELECT COUNT(c) " +
            "FROM Conversation c " +
            "WHERE c.user.userId = :userId AND c.active = true")
    Long countConversationByUserId(Long userId);
}
