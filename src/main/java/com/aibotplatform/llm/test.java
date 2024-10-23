package com.aibotplatform.llm;

public class test {
    public static void main(String[] args) {
        LLMSessionManager llmSessionManager = new LLMSessionManager();
        System.out.println(llmSessionManager.chat(1L,"GPT_3_5_TURBO","Hello,my name is Heisenberg."));
        System.out.println(llmSessionManager.chat(1L,"GPT_3_5_TURBO","Say my name"));
    }
}
