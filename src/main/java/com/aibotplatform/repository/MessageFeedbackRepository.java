package com.aibotplatform.repository;

import com.aibotplatform.model.MessageFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageFeedbackRepository extends JpaRepository<MessageFeedback, Long> {
    List<MessageFeedback> findByMessage_MessageId(Long messageId);

    Long countByCommenter_UserId(Long userId);
}
