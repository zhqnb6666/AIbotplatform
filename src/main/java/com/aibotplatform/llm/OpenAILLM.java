package com.aibotplatform.llm;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_3_5_TURBO;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_32K;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class OpenAILLM implements LLM {
    private OpenAiChatModel model;
    private final ChatMemory chatMemory = MessageWindowChatMemory.builder()
            .maxMessages(10)
            .build();
    private ChatBot assistant;

    public OpenAILLM(String modelName, List<AbstractMap.SimpleEntry<String, String>> chat_history) {
        initializeModel(modelName);
        initialize_messages(chat_history);
    }

    public OpenAILLM(String modelName, List<AbstractMap.SimpleEntry<String, String>> chat_history, String doc_path) {
        initializeRagModel(modelName, doc_path);
        initialize_messages(chat_history);
    }

    @Override
    public String chat(String message) {
        return assistant.chat(message);
    }

    private OpenAiChatModel getChatModel(OpenAiChatModelName modelName) {
        String api_key = "sk-6hMxxGzo2ZT6WzKXBa9cB82d964e4cAe9eE0F95d70C1Ba0e";
        return OpenAiChatModel.builder()
                .apiKey(api_key)
                .baseUrl("https://xiaoai.plus/v1")
                .modelName(modelName)
                .build();
    }

    private void chooseModel(String modelName) {
        switch (modelName.toUpperCase()) {
            case "GPT_3_5_TURBO":
                model = getChatModel(GPT_3_5_TURBO);
                break;
            case "GPT_4_32K":
                model = getChatModel(GPT_4_32K);
                break;
            case "GPT_4_O":
                model = getChatModel(GPT_4_O);
                break;
            case "GPT_4_O_MINI":
                model = getChatModel(GPT_4_O_MINI);
                break;
            default:
                throw new IllegalArgumentException("Unknown model name: " + modelName);
        }
    }

    private void initializeModel(String modelName) {
        chooseModel(modelName);
        assistant = AiServices.builder(ChatBot.class)
                .chatLanguageModel(model)
                .chatMemory(chatMemory)
                .build();
    }

    /**
     * 简化创建RAG模型的构建，在创建模型时根据是否输入文件路径来决定
     * @param modelName 模型名称
     * @param doc_path 文件路径
     */
    private void initializeRagModel(String modelName, String doc_path) {
        chooseModel(modelName);
        //外部知识库（文档）
        Document doc = FileSystemDocumentLoader.loadDocument(doc_path);
        //构建向量数据库
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        //嵌入文档
        EmbeddingStoreIngestor.ingest(doc, embeddingStore);

        assistant = AiServices.builder(ChatBot.class)
                .chatLanguageModel(model)
                .chatMemory(chatMemory)
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();
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
