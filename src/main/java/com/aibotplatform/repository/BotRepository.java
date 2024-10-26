package com.aibotplatform.repository;

import com.aibotplatform.model.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BotRepository extends JpaRepository<Bot, Long> {
    Optional<Bot> findByName(String name);
    List<Bot> findByIsActiveTrue();
    List<Bot> findBotsByCreator_UserIdAndIsActiveTrue(Long creatorId);
//    @Query("SELECT b FROM Bot b WHERE b.creator.userId = :creatorId AND b.type = 'CUSTOM' AND b.isActive = true")
//    List<Bot> findCustomBotsByCreator_UserId(@Param("creatorId") Long creatorId);
    long count();
    long countByType(Bot.BotType type);
}