package com.aibotplatform.dto.marketDTO;

import java.math.BigDecimal;

public record ChangeTokenRequest(
        BigDecimal creditAmount,
        Long tokenAmount,
        String description
) {
}
