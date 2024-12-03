package com.aibotplatform.service;

import org.springframework.web.bind.annotation.RequestParam;

public interface AdminService {
    String exportInfo();
    void rewardUser(Long userId, Long token);
}
