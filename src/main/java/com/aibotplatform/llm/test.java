package com.aibotplatform.llm;

public class test {
public static void main(String[] args) {
    LLMSessionManager llmSessionManager = new LLMSessionManager();

    long startTime = System.currentTimeMillis();
    System.out.println(llmSessionManager.chat(1L, "Stable-Diffusion-XL", "画一个松鼠鳜鱼"));
    long endTime = System.currentTimeMillis();
    System.out.println("Response time: " + (endTime - startTime) + " ms");

    ImageModel imageModel = new ImageModel();
    startTime = System.currentTimeMillis();
    System.out.println(imageModel.chat("画一个松鼠鳜鱼"));
    endTime = System.currentTimeMillis();
    System.out.println("Response time: " + (endTime - startTime) + " ms");

//    OpenAILLM openAILLM = new OpenAILLM("GPT_3_5_TURBO", null);
//
//    startTime = System.currentTimeMillis();
//    System.out.println(openAILLM.chat("Hello, my name is Heisenberg."));
//    endTime = System.currentTimeMillis();
//    System.out.println("Response time: " + (endTime - startTime) + " ms");
//
//    startTime = System.currentTimeMillis();
//    System.out.println(openAILLM.chat("Say my name"));
//    endTime = System.currentTimeMillis();
//    System.out.println("Response time: " + (endTime - startTime) + " ms");
}
}
