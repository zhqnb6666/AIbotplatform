package com.aibotplatform.llm;

public class test {
    public static void main(String[] args) {
        OpenAILLM openAILLM = new OpenAILLM();
        String response = openAILLM.chat("Hello");
        System.out.println(response);
    }
}
