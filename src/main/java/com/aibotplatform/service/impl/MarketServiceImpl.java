package com.aibotplatform.service.impl;

import com.aibotplatform.dto.marketDTO.ChangeTokenRequest;
import com.aibotplatform.dto.marketDTO.RechargeRequest;
import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.CreditHistory;
import com.aibotplatform.model.TokenHistory;
import com.aibotplatform.model.Transaction;
import com.aibotplatform.model.User;
import com.aibotplatform.repository.CreditHistoryRepository;
import com.aibotplatform.repository.TokenHistoryRepository;
import com.aibotplatform.repository.TransactionRepository;
import com.aibotplatform.service.MarketService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService {
    private final TokenHistoryRepository tokenHistoryRepository;
    private final TransactionRepository transactionRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final UserServiceImpl userService;

    @Override
    @Transactional
    public void recharge(RechargeRequest rechargeRequest, String username) {
        User user = userService.getUserByName(username);
        if (user == null) {
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        }
        Transaction transaction = new Transaction(
                null,
                user,
                rechargeRequest.amount(),
                rechargeRequest.paymentMethod(),
                Timestamp.from(Instant.now())
        );

        CreditHistory creditHistory = new CreditHistory(
                null,
                user,
                rechargeRequest.amount(),
                calculateNewCreditBalance(user, rechargeRequest.amount()),
                rechargeRequest.description(),
                Timestamp.from(Instant.now())
        );

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
    public void convertToToken(ChangeTokenRequest changeTokenRequest, String username) {
        User user = userService.getUserByName(username);
        if (user == null) {
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        }
        TokenHistory tokenHistory = new TokenHistory(
                null,
                user,
                changeTokenRequest.tokenAmount(),
                calculateNewTokenBalance(user, changeTokenRequest.tokenAmount()),
                changeTokenRequest.description(),
                Timestamp.from(Instant.now())
        );
        CreditHistory creditHistory = new CreditHistory(
                null,
                user,
                changeTokenRequest.creditAmount().negate(),
                calculateNewCreditBalance(user, changeTokenRequest.creditAmount().negate()),
                changeTokenRequest.description(),
                Timestamp.from(Instant.now())
        );
        if (creditHistory.getCreditBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new ApiException("Insufficient credits", HttpStatus.BAD_REQUEST);
        }
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

    private BigDecimal calculateNewCreditBalance(User user, BigDecimal changeAmount) {
        return user.getCredits().add(changeAmount);
    }
    private Long calculateNewTokenBalance(User user, Long changeAmount) {
        return user.getToken() + changeAmount;
    }
}
