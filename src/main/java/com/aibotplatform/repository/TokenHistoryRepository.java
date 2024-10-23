package com.aibotplatform.repository;

import com.aibotplatform.model.TokenHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenHistoryRepository extends JpaRepository<TokenHistory,Long>{
}
