package com.aibotplatform.llm;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 会话管理器，负责处理LLM实例的生命周期
@Service
public class LLMSessionManager {
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000; // 30分钟超时
    private final Map<Long, SessionInfo> activeSessions = new ConcurrentHashMap<>();

    private static class SessionInfo {
        LLM llm;
        long lastAccessTime;

        SessionInfo(LLM llm) {
            this.llm = llm;
            this.lastAccessTime = System.currentTimeMillis();
        }
    }

    public String chat(Long sessionId, String modelName, String input, List<AbstractMap.SimpleEntry<String, String>> history) {
        SessionInfo session = activeSessions.get(sessionId);

        if (session == null || isSessionExpired(session)) {
            // 创建新会话
            LLM newLLM = LLMFactory.createLLM(modelName, history);
            session = new SessionInfo(newLLM);
            activeSessions.put(sessionId, session);
        }
        session.lastAccessTime = System.currentTimeMillis();
        return session.llm.chat(input);
    }

    public String chat(String modelName, String input,List<AbstractMap.SimpleEntry<String, String>>history) {
        LLM newLLM = LLMFactory.createLLM(modelName, history);
        return newLLM.chat(input);
    }

    private boolean isSessionExpired(SessionInfo session) {
        return System.currentTimeMillis() - session.lastAccessTime > SESSION_TIMEOUT;
    }

    // 定期清理过期会话
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void cleanupExpiredSessions() {
        activeSessions.entrySet().removeIf(entry ->
                isSessionExpired(entry.getValue()));
    }
}