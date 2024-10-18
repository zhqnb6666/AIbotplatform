package com.aibotplatform.dto.marketDTO;

import java.math.BigDecimal;

public record RechargeRequest(
        BigDecimal amount,
        String paymentMethod,
        String description
) {
}
