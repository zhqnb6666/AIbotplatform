package com.aibotplatform.service.impl;

import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.CreditHistory;
import com.aibotplatform.model.TokenHistory;
import com.aibotplatform.model.Transaction;
import com.aibotplatform.repository.CreditHistoryRepository;
import com.aibotplatform.repository.TokenHistoryRepository;
import com.aibotplatform.repository.TransactionRepository;
import com.aibotplatform.service.MarketService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class MarketServiceImpl implements MarketService {

    private final TokenHistoryRepository tokenHistoryRepository;
    private final TransactionRepository transactionRepository;

    private final CreditHistoryRepository creditHistoryRepository;

    private final UserService userService;

    public MarketServiceImpl(TokenHistoryRepository tokenHistoryRepository, TransactionRepository transactionRepository, CreditHistoryRepository creditHistoryRepository, UserService userService) {
        this.tokenHistoryRepository = tokenHistoryRepository;
        this.transactionRepository = transactionRepository;
        this.creditHistoryRepository = creditHistoryRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void recharge(Transaction transaction,CreditHistory creditHistory) {
        transaction.setTransactionId(null);
        transaction.setCreatedAt(Timestamp.from(Instant.now()));
        creditHistory.setCreditHistoryId(null);
        creditHistory.setCreatedAt(Timestamp.from(Instant.now()));
        try {
             transactionRepository.save(transaction);
             creditHistoryRepository.save(creditHistory);
             userService.changeCredits(transaction.getUser(), creditHistory.getCreditBalance());
        } catch (Exception e) {
            throw new ApiException("Recharge failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void convertToToken(TokenHistory tokenHistory, CreditHistory creditHistory) {
        tokenHistory.setTokenHistoryId(null);
        tokenHistory.setCreatedAt(Timestamp.from(Instant.now()));
        creditHistory.setCreditHistoryId(null);
        creditHistory.setCreatedAt(Timestamp.from(Instant.now()));
        try {
            tokenHistoryRepository.save(tokenHistory);
            creditHistoryRepository.save(creditHistory);
            userService.changeTokens(tokenHistory.getUser(), tokenHistory.getTokenBalance());
            userService.changeCredits(tokenHistory.getUser(), creditHistory.getCreditBalance());
        } catch (Exception e) {
            throw new ApiException("Token conversion failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
