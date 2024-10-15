package com.aibotplatform.dto;

import java.math.BigDecimal;

public record RechargeDTO(
        Long rechargeId,
        Long userId,
        BigDecimal amount,
        String transactionType,
        String description
) {
}
