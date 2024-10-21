package com.aibotplatform.repository;

import com.aibotplatform.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    @Query("SELECT SUM(t.amount) FROM Transaction t")
    BigDecimal sumByAmount();
}
