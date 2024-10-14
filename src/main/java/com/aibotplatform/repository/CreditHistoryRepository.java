package com.aibotplatform.repository;

import com.aibotplatform.model.CreditHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditHistoryRepository extends JpaRepository<CreditHistory,Long> {
}
