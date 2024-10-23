package com.aibotplatform.llm;
import java.util.AbstractMap;
import java.util.List;

public class LLMFactory {
    // 使用枚举定义支持的模型类型
    public enum ModelType {
        GPT_3_5("GPT_3_5_TURBO"),
        GPT_4("GPT_4"),
        ERNIE_BOT("ERNIE-Bot"),
        ERNIE_BOT_4("ERNIE-Bot-4");

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

        switch (type) {
            case GPT_3_5:
                return new OpenAILLM(modelName, chatHistory);
            case GPT_4:
            case ERNIE_BOT:
            case ERNIE_BOT_4:
                return new QianFanLLM(modelName, chatHistory);
            default:
                throw new IllegalArgumentException("Unsupported model type: " + modelName);
        }
    }
}