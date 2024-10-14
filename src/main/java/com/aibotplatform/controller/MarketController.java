package com.aibotplatform.controller;

import com.aibotplatform.dto.RechargeDTO;
import com.aibotplatform.dto.ChangeTokenDTO;
import com.aibotplatform.model.CreditHistory;
import com.aibotplatform.model.TokenHistory;
import com.aibotplatform.model.Transaction;
import com.aibotplatform.model.User;
import com.aibotplatform.service.MarketService;
import com.aibotplatform.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/market")
@Tag(name = "Market API", description = "APIs for managing transactions and token conversions")
public class MarketController {

    private final MarketService marketService;
    private final UserService userService;

    public MarketController(MarketService marketService, UserService userService) {
        this.marketService = marketService;
        this.userService = userService;
    }

    @PostMapping("/recharge")
    @Operation(summary = "Recharge credits", description = "Add credits to a user's account")
    public ResponseEntity<Void> recharge(
            @RequestBody RechargeDTO rechargeDTO) {
        Transaction transaction = convertToTransaction(rechargeDTO);
        CreditHistory creditHistory = convertToCreditHistory(rechargeDTO);
        marketService.recharge(transaction, creditHistory);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/convert-to-token")
    @Operation(summary = "Convert credits to tokens", description = "Convert user's credits into tokens")
    public ResponseEntity<Void> convertToToken(
            @RequestBody ChangeTokenDTO changeTokenDTO) {
        CreditHistory creditHistory = convertToCreditHistory(changeTokenDTO);
        TokenHistory tokenHistory = convertToTokenHistory(changeTokenDTO);
        marketService.convertToToken(tokenHistory, creditHistory);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private Transaction convertToTransaction(RechargeDTO rechargeDTO) {
        return new Transaction(
                null,
                userService.getUserById(rechargeDTO.userId()),
                rechargeDTO.amount(),
                rechargeDTO.description()
        );

    }

    private TokenHistory convertToTokenHistory(ChangeTokenDTO changeTokenDTO) {
        User user = userService.getUserById(changeTokenDTO.userId());
        return new TokenHistory(
                null,
                user,
                changeTokenDTO.tokenAmount(),
                calculateNewTokenBalance(user, changeTokenDTO.tokenAmount()),
                changeTokenDTO.description()
        );
    }

    private CreditHistory convertToCreditHistory(RechargeDTO rechargeDTO) {
        User user = userService.getUserById(rechargeDTO.userId());
        return new CreditHistory(
                null,
                userService.getUserById(rechargeDTO.userId()),
                rechargeDTO.amount(),
                calculateNewCreditBalance(user, rechargeDTO.amount()),
                rechargeDTO.description()
        );
    }

    private CreditHistory convertToCreditHistory(ChangeTokenDTO changeTokenDTO) {
        User user = userService.getUserById(changeTokenDTO.userId());
        return new CreditHistory(
                null,
                user,
                changeTokenDTO.creditAmount().negate(),
                calculateNewCreditBalance(user, changeTokenDTO.creditAmount().negate()),
                changeTokenDTO.description()
        );
    }

    private Long calculateNewTokenBalance(User user, Long changeAmount) {
        return user.getToken() + changeAmount;
    }

    private BigDecimal calculateNewCreditBalance(User user, BigDecimal changeAmount) {
        return user.getCredits().add(changeAmount);
    }
}
