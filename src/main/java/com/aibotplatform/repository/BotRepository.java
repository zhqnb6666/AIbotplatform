package com.aibotplatform.repository;

import com.aibotplatform.model.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BotRepository extends JpaRepository<Bot, Long> {
    Bot findByName(String name);
    Bot findByModel(String model);
    List<Bot> findByIsActiveTrue();
    List<Bot> findBotsByCreator_UserId(Long creatorId);
    long count();
    long countByType(Bot.BotType type);
}