package com.aibotplatform.repository;

import com.aibotplatform.model.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BotRepository extends JpaRepository<Bot, Long> {
    Optional<Bot> findByName(String name);
    List<Bot> findByIsActiveTrue();
    List<Bot> findBotsByCreator_UserId(Long creatorId);
    long count();
    long countByType(Bot.BotType type);
}