package com.aibotplatform.repository;

import com.aibotplatform.model.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BotRepository extends JpaRepository<Bot, Long> {
    Optional<Bot> findByName(String name);
    List<Bot> findByIsActiveTrue();
    List<Bot> findBotsByCreator_UserId(Long creatorId);
//    @Query("SELECT b FROM Bot b WHERE b.creator.userId = :creatorId AND b.type = 'CUSTOM'")
//    List<Bot> findCustomBotsByCreator_UserId(@Param("creatorId") Long creatorId);
    List<Bot> findBotsByCreator_UserIdAndIsActiveTrue(Long creatorId);

    @Query("SELECT b FROM Bot b WHERE b.isActive = true ORDER BY b.createdAt DESC LIMIT :top")
    List<Bot> findLatestBots(@Param("top") Integer top);

    @Query("SELECT b, COUNT(c.bot.botId) AS access_count \n" +
            "FROM Bot b LEFT JOIN Conversation c ON b.botId = c.bot.botId \n" +
            "GROUP BY b.botId \n" +
            "ORDER BY access_count DESC \n" +
            "LIMIT :top")
    List<Bot> findMostPopularBots(@Param("top") Integer top);

    @Query("SELECT b, COALESCE(AVG(br.rating), 0) AS avg_rating \n" +
            "FROM Bot b LEFT JOIN BotRating br ON b.botId = br.bot.botId \n" +
            "GROUP BY b.botId \n" +
            "ORDER BY avg_rating DESC \n" +
            "LIMIT :top")
    List<Bot> findHistoricalBestBots(@Param("top") Integer top);

    @Query("SELECT b, COALESCE(AVG(br.rating), 0) AS avg_rating \n" +
            "FROM Bot b LEFT JOIN BotRating br ON b.botId = br.bot.botId AND \n" +
            "br.createdAt >= :oneMonthAgo \n" +
            "GROUP BY b.botId \n" +
            "ORDER BY avg_rating DESC \n" +
            "LIMIT :top")
    List<Bot> findMonthlyBestBots(@Param("oneMonthAgo") LocalDateTime oneMonthAgo, @Param("top") Integer top);

    long count();
    long countByType(Bot.BotType type);
    List<Bot> findBotsByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}