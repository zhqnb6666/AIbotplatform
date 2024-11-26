package com.aibotplatform.repository;

import com.aibotplatform.model.UserFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFeedbackRepository extends JpaRepository<UserFeedback, Long> {
    List<UserFeedback> findByUser_UserId(Long userId);
    Long countByUser_UserId(Long userId);
}
