package com.aibotplatform.llm;

import org.aspectj.apache.bcel.classfile.Module;
import reactor.core.publisher.SignalType;

public class test {
public static void main(String[] args) {
//    LLMSessionManager llmSessionManager = new LLMSessionManager();
//
    long startTime = System.currentTimeMillis();
//    System.out.println(llmSessionManager.chat(1L, "Stable-Diffusion-XL", "画一个松鼠鳜鱼"));
    long endTime = System.currentTimeMillis();
//    System.out.println("Response time: " + (endTime - startTime) + " ms");
//
    ImageModel imageModel = new ImageModel();
    startTime = System.currentTimeMillis();
    System.out.println(imageModel.chat("画一个松鼠鳜鱼"));
    endTime = System.currentTimeMillis();
    System.out.println("Response time: " + (endTime - startTime) + " ms");
//
//    CalculatorBot calculatorBot = new CalculatorBot();
//    System.out.println(calculatorBot.chat("计算下列值，(sin(34)+cos(32))^2"));


//    OpenAILLM openAILLM = new OpenAILLM("GPT_4_O_MINI", null);
//
////    startTime = System.currentTimeMillis();
//    openAILLM.chat("Hello")
//            .subscribe(
//                    System.out::print,
//                    error -> System.err.println("Error: " + error),  // onError
//                    () -> {
//                        System.out.println("\nStream completed!");
//                    }
//            );

//    QianFanLLM qianFanLLM = new QianFanLLM("ERNIE-Bot", null);
//    qianFanLLM.chat("Hello")
//            .subscribe(
//                    System.out::print,
//                    error -> System.err.println("Error: " + error),  // onError
//                    () -> {
//                        System.out.println("\nStream completed!");
//                    }
//            );
//    endTime = System.currentTimeMillis();
//    System.out.println("Response time: " + (endTime - startTime) + " ms");
//
//    startTime = System.currentTimeMillis();
//    System.out.println(openAILLM.chat("Say my name"));
//    endTime = System.currentTimeMillis();
//    System.out.println("Response time: " + (endTime - startTime) + " ms");
}
}
