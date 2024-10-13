package com.aibotplatform.service.impl;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//@Service
//public class VerificationService {
//    private final Map<String, String> verificationCodes = new HashMap<>();
//    private static final long CODE_EXPIRATION_TIME = 300000; // 5 minutes
//    private static final long RESEND_COOLDOWN = 60000; // 1 minute
//
//    public String generateVerificationCode(String email) {
//        String code = String.format("%06d", new Random().nextInt(999999));
//        verificationCodes.put(email, code);
//        return code;
//    }
//
//    public boolean verifyCode(String email, String code) {
//        String storedCode = verificationCodes.get(email);
//        return storedCode != null && storedCode.equals(code);
//    }
//
//    public void clearCode(String email) {
//        verificationCodes.remove(email);
//    }
//}
@Service
public class VerificationService {
    private final Map<String, VerificationCodeInfo> verificationCodes = new HashMap<>();
    private static final long CODE_EXPIRATION_TIME = 1800000; // 30 minutes
    private static final long RESEND_COOLDOWN = 60000; // 1 minute

    public String generateVerificationCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));
        long currentTime = System.currentTimeMillis();
        verificationCodes.put(email, new VerificationCodeInfo(code, currentTime));
        return code;
    }

    public boolean canResendCode(String email) {
        VerificationCodeInfo info = verificationCodes.get(email);
        return info == null || System.currentTimeMillis() - info.timestamp > RESEND_COOLDOWN;
    }

    public boolean verifyCode(String email, String code) {
        VerificationCodeInfo info = verificationCodes.get(email);
        if (info == null) return false;
        long currentTime = System.currentTimeMillis();
        return info.code.equals(code) && (currentTime - info.timestamp <= CODE_EXPIRATION_TIME);
    }

    public void clearCode(String email) {
        verificationCodes.remove(email);
    }

    private static class VerificationCodeInfo {
        String code;
        long timestamp;

        VerificationCodeInfo(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }
}
