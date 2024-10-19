package com.aibotplatform.llm;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.qianfan.QianfanChatModel;
import dev.langchain4j.service.AiServices;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class QianFanLLM {
    private final String ak = "uMF5PVIQDQYY58QZJ0J04XrF";
    private final String sk = "zzNMgEl8pDpDBEQLVpawuQLRzRnYkVh1";
    private String model_name = "ERNIE-Bot";
    private ChatMemory chatMemory = MessageWindowChatMemory.builder()
            .maxMessages(10)
            .build();
    private QianfanChatModel model = QianfanChatModel.builder()
            .apiKey(ak)
            .secretKey(sk)
            .modelName(model_name)
            .build();
    private ChatBot assistant = AiServices.builder(ChatBot.class)
            .chatLanguageModel(model) // the model
            .chatMemory(chatMemory)  // memory
            .build();

    public QianFanLLM(){}

    public QianFanLLM(String model_name){
        this.model_name = model_name;
        this.model = QianfanChatModel.builder()
                .apiKey(ak)
                .secretKey(sk)
                .modelName(model_name)
                .build();
        this.chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();

        this.assistant = AiServices.builder(ChatBot.class)
                .chatLanguageModel(model) // the model
                .chatMemory(chatMemory)// memory
                .build();
    }

    public String chat(String userMessage) {
        return assistant.chat(userMessage);
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
