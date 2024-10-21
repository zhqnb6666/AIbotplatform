package com.aibotplatform.llm;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_3_5_TURBO;

public class OpenAILLM {
    private final OpenAiChatModel model = OpenAiChatModel.builder()
            .apiKey("sk-S4h2bK7x8XYrFJCUFf6dCe3eE81142Dd840a037aA261E035")
            .baseUrl("https://xiaoai.plus/v1")
            .modelName(GPT_3_5_TURBO)
            .build();
    private final ChatMemory chatMemory = MessageWindowChatMemory.builder()
            .maxMessages(10)
            .build();
    private final ChatBot assistant = AiServices.builder(ChatBot.class)
            .chatLanguageModel(model) // the model
            .chatMemory(chatMemory)  // memory
            .build();

    public String chat(String message) {
        return assistant.chat(message);
    }

    public void initialize_messages(List<AbstractMap.SimpleEntry<String, String>> chat_history) {
        if (chat_history == null) {
            return;
        }
        chatMemory.clear();
        for (AbstractMap.SimpleEntry<String, String> entry : chat_history) {
            if (entry.getKey().equals("user")) {
                chatMemory.add(new UserMessage(entry.getValue()));
            } else if (entry.getKey().equals("assistant")) {
                chatMemory.add(new AiMessage(entry.getValue()));
            }
        }
    }

    public List<AbstractMap.SimpleEntry<String, String>> getChatHistory() {
        List<AbstractMap.SimpleEntry<String, String>> chatHistory = new ArrayList<>();
        for (ChatMessage chatMessage : chatMemory.messages()) {
            if (chatMessage instanceof UserMessage) {
                chatHistory.add(new AbstractMap.SimpleEntry<>("user", chatMessage.text()));
            } else if (chatMessage instanceof AiMessage) {
                chatHistory.add(new AbstractMap.SimpleEntry<>("assistant", chatMessage.text()));
            }
        }
        return chatHistory;
    }
}
