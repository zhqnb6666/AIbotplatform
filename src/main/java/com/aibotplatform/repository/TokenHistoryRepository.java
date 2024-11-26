package com.aibotplatform.repository;

import com.aibotplatform.model.TokenHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenHistoryRepository extends JpaRepository<TokenHistory,Long>{
    @Query("SELECT SUM(t.tokenChange) " +
            "FROM TokenHistory t " +
            "WHERE t.user.userId = :userId AND t.tokenChange < 0")
    Long sumTokensByUserId(Long userId);
}
