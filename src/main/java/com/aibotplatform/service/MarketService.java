package com.aibotplatform.service;

import com.aibotplatform.model.CreditHistory;
import com.aibotplatform.model.TokenHistory;
import com.aibotplatform.model.Transaction;

public interface MarketService {
    void recharge(Transaction transaction, CreditHistory creditHistory);
    void convertToToken(TokenHistory tokenHistory, CreditHistory creditHistory);
}
