package com.aibotplatform.service;

import com.aibotplatform.dto.marketDTO.ChangeTokenRequest;
import com.aibotplatform.dto.marketDTO.RechargeRequest;
import com.aibotplatform.model.CreditHistory;
import com.aibotplatform.model.TokenHistory;
import com.aibotplatform.model.Transaction;

public interface MarketService {
    void recharge(RechargeRequest rechargeRequest, String username);
    void convertToToken(ChangeTokenRequest changeTokenRequest, String username);
}
