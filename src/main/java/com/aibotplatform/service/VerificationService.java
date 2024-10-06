package com.aibotplatform.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class VerificationService {
    private final Map<String, String> verificationCodes = new HashMap<>();

    public String generateVerificationCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));
        verificationCodes.put(email, code);
        return code;
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        return storedCode != null && storedCode.equals(code);
    }

    public void clearCode(String email) {
        verificationCodes.remove(email);
    }
}