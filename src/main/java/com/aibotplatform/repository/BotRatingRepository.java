package com.aibotplatform.repository;

import com.aibotplatform.model.Bot;
import com.aibotplatform.model.BotRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BotRatingRepository extends JpaRepository<BotRating,Long>{
    List<BotRating> getBotRatingsByBot(Bot bot);
    List<BotRating> getBotRatingsByBot_BotId(Long botId);

}
