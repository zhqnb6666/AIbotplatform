package com.aibotplatform.llm;
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

        Stable_Diffusion_XL("Stable-Diffusion-XL");


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

    public static LLM createLLM(String modelName, List<AbstractMap.SimpleEntry<String, String>> chatHistory) {
        ModelType type = ModelType.fromModelName(modelName);

        return switch (type) {
            case GPT_3_5, GPT_4_32K, GPT_4_O, GPT_4_O_MINI ->
                    new OpenAILLM(modelName, chatHistory);
            case ERNIE_BOT, BLOOMZ_7B, Llama_2_7B, Llama_2_13B, Llama_2_70B, Chinese_Llama_2_7B, ChatGLM, Aquila ->
                    new QianFanLLM(modelName, chatHistory);
            case Stable_Diffusion_XL ->
                    new ImageModel();
        };
    }

    public static LLM createRagLLM(String modelName, List<AbstractMap.SimpleEntry<String, String>> chatHistory, String doc_path) {
        ModelType type = ModelType.fromModelName(modelName);

        return switch (type) {
            case GPT_3_5, GPT_4_32K, GPT_4_O, GPT_4_O_MINI ->
                    new OpenAILLM(modelName, chatHistory, doc_path);
            case ERNIE_BOT, BLOOMZ_7B, Llama_2_7B, Llama_2_13B, Llama_2_70B, Chinese_Llama_2_7B, ChatGLM, Aquila ->
                    new QianFanLLM(modelName, chatHistory, doc_path);
            case Stable_Diffusion_XL ->
                    new ImageModel();
        };
    }
}