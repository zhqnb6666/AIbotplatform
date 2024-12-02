package com.aibotplatform.llm;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.qianfan.QianfanChatModel;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Util {
    private final ChatLanguageModel chatModel;
    public Util() {
        String AK = "uMF5PVIQDQYY58QZJ0J04XrF";
        String SK = "zzNMgEl8pDpDBEQLVpawuQLRzRnYkVh1";
        this.chatModel = QianfanChatModel.builder()
                .apiKey(AK)
                .secretKey(SK)
                .modelName("ERNIE-Bot")
                .temperature(0.5)
                .build();
    }
    public List<String> predictNextQuestions(String userQuestion) {
        userQuestion = userQuestion.replaceAll("^\"|\"$", "");
        String nextQuestionPrompt =
                "Based on the user's question: '{{question}}', " +
                        "predict three potential follow-up questions a user might ask. " +
                        "Provide the questions as a numbered list.";

        PromptTemplate promptTemplate = PromptTemplate.from(nextQuestionPrompt);
        Prompt prompt = promptTemplate.apply(Map.of("question", userQuestion));
        String responseText = chatModel.generate(prompt.text());
        return responseText.lines()
                .map(String::trim)
                .filter(s -> !s.isEmpty() && (s.startsWith("1.") || s.startsWith("2.") || s.startsWith("3.")))
                .map(s -> s.replaceFirst("^[0-9]\\.", "").trim())
                .collect(Collectors.toList());
    }

    public String predictTitle(String userQuestion) {
        userQuestion = userQuestion.replaceAll("^\"|\"$", "");
        String titlePrompt =
                "Based on the user's question: '{{question}}', " +
                        "generate a concise and descriptive title for the conversation. " +
                        "The title should capture the essence of the question.";
        PromptTemplate promptTemplate = PromptTemplate.from(titlePrompt);
        Prompt prompt = promptTemplate.apply(Map.of("question", userQuestion));
        String response = chatModel.generate(prompt.text());
        return response.trim();
    }

    public static void main(String[] args) {
        Util util = new Util();
        String testQuestion = "你好";
        List<String> nextQuestions = util.predictNextQuestions(testQuestion);
        System.out.println("Predicted Next Questions:");
        nextQuestions.forEach(System.out::println);
        String title = util.predictTitle(testQuestion);
        System.out.println("\nPredicted Title:");
        System.out.println(title);
    }
}