package com.aibotplatform.llm;
import com.aibotplatform.model.Bot;
import jakarta.validation.constraints.Null;

import java.util.AbstractMap;
import java.util.List;

public class LLMFactory {
    // 使用枚举定义支持的模型类型
    public enum ModelType {
        GPT_3_5("GPT_3_5_TURBO"),
        GPT_4_32K("GPT_4_32K"),
        GPT_4_O("GPT_4_O"),
        GPT_4_O_MINI("GPT_4_O_MINI"),
        ERNIE_BOT("ERNIE-Bot"),
        BLOOMZ_7B("BLOOMZ-7B"),
        Llama_2_7B("Llama-2-7b-chat"),
        Llama_2_13B("Llama-2-13b-chat"),
        Llama_2_70B("Llama-2-70b-chat"),
        Chinese_Llama_2_7B("Qianfan-Chinese-Llama-2-7B"),
        ChatGLM("ChatGLM2-6B-32K"),
        Aquila("AquilaChat-7B"),
        Stable_Diffusion_XL("Stable-Diffusion-XL"),
        Calculator_Bot("Calculator-Bot");

        private String modelName;

        ModelType(String modelName) {
            this.modelName = modelName;
        }

        public String getModelName() {
            return modelName;
        }

        public static ModelType fromModelName(String modelName) {
            for (ModelType type : values()) {
                if (type.getModelName().equals(modelName)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unsupported model: " + modelName);
        }
    }

    public static LLM createLLM(Bot bot, List<AbstractMap.SimpleEntry<String, String>> chatHistory) {
        String promptTemplate = bot.getPromptTemplate();
        String systemMessage = (promptTemplate == null || promptTemplate.trim().isEmpty()) ? "You are a helpful AI assistant. Analyze problems step by step and provide clear, concise answers." : promptTemplate;
        String modelName = bot.getModel();
        ModelType type = ModelType.fromModelName(modelName);
        return switch (type) {
            case GPT_3_5, GPT_4_32K, GPT_4_O, GPT_4_O_MINI ->
                    new OpenAILLM(modelName, bot.getTemperature(), chatHistory, systemMessage);
            case ERNIE_BOT, BLOOMZ_7B, Llama_2_7B, Llama_2_13B, Llama_2_70B, Chinese_Llama_2_7B, ChatGLM, Aquila ->
                    new QianFanLLM(modelName, bot.getTemperature(), chatHistory, systemMessage);
            case Stable_Diffusion_XL ->
                    new ImageModel();
            case Calculator_Bot -> new CalculatorBot();
        };
    }

    public static LLM createRagLLM(Bot bot, List<AbstractMap.SimpleEntry<String, String>> chatHistory, String doc_path) {
        String modelName = bot.getName();
        ModelType type = ModelType.fromModelName(modelName);

        return switch (type) {
            case GPT_3_5, GPT_4_32K, GPT_4_O, GPT_4_O_MINI ->
                    new OpenAILLM(modelName, bot.getTemperature(), chatHistory, "", doc_path);
            case ERNIE_BOT, BLOOMZ_7B, Llama_2_7B, Llama_2_13B, Llama_2_70B, Chinese_Llama_2_7B, ChatGLM, Aquila ->
                    new QianFanLLM(modelName, bot.getTemperature(), chatHistory, "", doc_path);
            case Stable_Diffusion_XL ->
                    new ImageModel();
            case Calculator_Bot -> new CalculatorBot();

        };
    }
}