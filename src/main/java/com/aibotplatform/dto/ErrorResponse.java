package com.aibotplatform.dto;

public record ErrorResponse(
        String code,
        String message
) {
}