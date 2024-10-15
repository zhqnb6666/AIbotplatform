package com.aibotplatform.dto;


public record UserFeedbackDTO(Long userFeedbackId,
                              Long UserId,
                              Long commenterId,
                              String content,
                              Integer rating) {
}