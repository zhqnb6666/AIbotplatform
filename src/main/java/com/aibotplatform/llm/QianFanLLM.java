package com.aibotplatform.llm;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.qianfan.*;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import reactor.core.publisher.Flux;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class QianFanLLM implements LLM {
    private static final String AK = "uMF5PVIQDQYY58QZJ0J04XrF";
    private static final String SK = "zzNMgEl8pDpDBEQLVpawuQLRzRnYkVh1";
    private final ChatMemory chatMemory;
    private final LLM assistant;

    public QianFanLLM(String modelName, Double temperature, List<AbstractMap.SimpleEntry<String, String>> chatHistory,String systemMessage) {
        this.chatMemory = MessageWindowChatMemory.builder().maxMessages(10).build();
        QianfanStreamingChatModel model = QianfanStreamingChatModel.builder()
                .apiKey(AK)
                .secretKey(SK)
                .modelName(modelName)
                .temperature(temperature)
                .build();
        initializeMessages(chatHistory);
        this.assistant = AiServices.builder(LLM.class)
                .streamingChatLanguageModel(model)
                .chatMemory(chatMemory)
                .systemMessageProvider(content -> systemMessage)
                .build();
    }

    /**
     * 简化了检索增强生成模型的构建，直接集成在创建模型时根据是否输入文件路径来决定
     * @param modelName 模型名称
     * @param temperature 温度
     * @param chatHistory 历史聊天记录
     * @param doc_path 文件地址
     */
    public QianFanLLM(String modelName, Double temperature, List<AbstractMap.SimpleEntry<String, String>> chatHistory,String systemMessage, String doc_path) {
        this.chatMemory = MessageWindowChatMemory.builder().maxMessages(10).build();
        QianfanStreamingChatModel model = QianfanStreamingChatModel.builder()
                .apiKey(AK)
                .secretKey(SK)
                .modelName(modelName)
                .temperature(temperature)
                .build();
        initializeMessages(chatHistory);
        Document doc = FileSystemDocumentLoader.loadDocument(doc_path);
        //构建向量数据库
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        //嵌入文档
        EmbeddingStoreIngestor.ingest(doc, embeddingStore);
        this.assistant = AiServices.builder(LLM.class)
                .streamingChatLanguageModel(model)
                .chatMemory(chatMemory)
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .systemMessageProvider(content -> systemMessage)
                .build();
    }

    @Override
    public Flux<String> chat(String userMessage) {
        return assistant.chat(userMessage);
    }

    public void initializeMessages(List<AbstractMap.SimpleEntry<String, String>> chat_history) {
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
