package com.aibotplatform.llm;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_3_5_TURBO;

public class RAG {
    public Assistant getRagAssitant(String dco_path){
        //基座模型
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey("sk-S4h2bK7x8XYrFJCUFf6dCe3eE81142Dd840a037aA261E035")
                .baseUrl("https://xiaoai.plus/v1")
                .modelName(GPT_3_5_TURBO)
                .build();
        //外部知识库（文档）
        Document doc = FileSystemDocumentLoader.loadDocument(dco_path);
        //构建向量数据库
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        //嵌入文档
        EmbeddingStoreIngestor.ingest(doc, embeddingStore);
        //返回Agent
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();
    }
}
