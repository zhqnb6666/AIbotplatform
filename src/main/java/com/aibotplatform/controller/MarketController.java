package com.aibotplatform.controller;

import com.aibotplatform.dto.marketDTO.RechargeRequest;
import com.aibotplatform.dto.marketDTO.ChangeTokenRequest;
import com.aibotplatform.exception.ApiException;
import com.aibotplatform.service.MarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/market")
@Tag(name = "Market API", description = "APIs for managing transactions and token conversions")
public class MarketController {

    private final MarketService marketService;

    @PostMapping("/recharge")
    @Operation(summary = "Recharge credits", description = "Add credits to a user's account")
    public ResponseEntity<?> recharge(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody RechargeRequest rechargeRequest) {
        try {
            marketService.recharge(rechargeRequest, userDetails.getUsername());
        } catch (ApiException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/convert-to-token")
    @Operation(summary = "Convert credits to tokens", description = "Convert user's credits into tokens")
    public ResponseEntity<?> convertToToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangeTokenRequest changeTokenRequest) {
        try {
            marketService.convertToToken(changeTokenRequest, userDetails.getUsername());
        } catch (ApiException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
