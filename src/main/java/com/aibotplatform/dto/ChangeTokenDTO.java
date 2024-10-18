package com.aibotplatform.dto;

import java.math.BigDecimal;

public record ChangeTokenDTO(
        Long changeTokenId,
        Long userId,
        BigDecimal creditAmount,
        Long tokenAmount,

        String description
) {
}
