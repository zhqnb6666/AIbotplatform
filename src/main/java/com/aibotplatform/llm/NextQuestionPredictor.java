package com.aibotplatform.llm;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.model.chat.ChatResponse;

import java.util.List;

import static com.baidubce.qianfan.core.auth.Auth.TYPE_OAUTH;

public class NextQuestionPredictor {
    private static final String ak = "uMF5PVIQDQYY58QZJ0J04XrF";
    private static final String sk = "zzNMgEl8pDpDBEQLVpawuQLRzRnYkVh1";
    private final Qianfan qianfan = new Qianfan(TYPE_OAUTH,ak, sk);
    private final String CoT = "user：'支持向量机是什么？'请你根据用户的这个问题，推测用户可能会继续询问的下一个问题，给出三个可能" +
            "assistant：1. 支持向量机的运行机制和原理是什么？2. 在实际应用中，支持向量机主要应用于哪些领域或场景？3. 如何利用支持向量机来解决实际问题，例如分类、回归或预测任务？";
    private final String system_prompt = "请你根据用户的这个问题，推测用户可能会继续询问的下一个问题，给出三个可能。 assistant：";

    /**
     * 根据用户的问题推测用户可能感兴趣的下一个问题，返回一个具有三个问题的列表，每次调用耗时大概3-4S，建议与chat方法同步调用
     * @param user_question
     * @return List<String>
     */
    public List<String> predict(String user_question) {
        ChatResponse resp = qianfan.chatCompletion()
                .model("Yi-34B-Chat")
                .addMessage("user",CoT + "user：'" + user_question + "'" + system_prompt)
                .execute();
        String ans = resp.getResult();

        return List.of(ans.split("\n"));
    }
}
