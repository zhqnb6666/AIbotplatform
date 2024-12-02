package com.aibotplatform.llm;

import com.aibotplatform.model.Conversation;
import com.aibotplatform.service.ConversationService;
import com.aibotplatform.service.impl.ConversationServiceImpl;
import org.aspectj.apache.bcel.classfile.Module;
import reactor.core.publisher.SignalType;

public class test {

public static void main(String[] args) {
//    LLMSessionManager llmSessionManager = new LLMSessionManager();
//
//    long startTime = System.currentTimeMillis();
////    System.out.println(llmSessionManager.chat(1L, "Stable-Diffusion-XL", "画一个松鼠鳜鱼"));
//    long endTime = System.currentTimeMillis();
////    System.out.println("Response time: " + (endTime - startTime) + " ms");
////
//    ImageModel imageModel = new ImageModel();
//    startTime = System.currentTimeMillis();
//    System.out.println(imageModel.chat("画一个松鼠鳜鱼"));
//    endTime = System.currentTimeMillis();
//    System.out.println("Response time: " + (endTime - startTime) + " ms");
//
//    CalculatorBot calculatorBot = new CalculatorBot();
//    calculatorBot.chat("计算下列值，(sin(34)+cos(32))^2")
//            .subscribe(
//                    System.out::print,
//                    error -> System.err.println("Error: " + error),  // onError
//                    () -> {
//                        System.out.println("\nStream completed!");
//                    }
//            );

//    System.out.println(calculatorBot.chat("计算下列值，(sin(34)+cos(32))^2"));


//    OpenAILLM openAIRagLLM = new OpenAILLM("GPT_4_O_MINI",0.8, null,"","D:\\Document\\project2_libtensor.pdf");
//
////    startTime = System.currentTimeMillis();
//    openAIRagLLM.chat(" Basic Requirements占多少的分值")
//            .subscribe(
//                    System.out::print,
//                    error -> System.err.println("Error: " + error),  // onError
//                    () -> {
//                        System.out.println("\nStream completed!");
//                    }
//            );
//    OpenAILLM openAILLM = new OpenAILLM("GPT_4_O_MINI",0.8, null,"用中文回答用户的问题");

//    startTime = System.currentTimeMillis();
//    openAILLM.chat("Basic Requirements占多少的分值")
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
