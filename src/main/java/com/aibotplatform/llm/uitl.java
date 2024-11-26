package com.aibotplatform.llm;

import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.model.chat.ChatResponse;

import java.util.List;
import java.util.stream.Collectors;

import static com.baidubce.qianfan.core.auth.Auth.TYPE_OAUTH;

public class uitl {
    private static final String ak = "uMF5PVIQDQYY58QZJ0J04XrF";
    private static final String sk = "zzNMgEl8pDpDBEQLVpawuQLRzRnYkVh1";
    private final Qianfan qianfan = new Qianfan(TYPE_OAUTH,ak, sk);
    private final String nq_CoT = "user：'支持向量机是什么？'请你根据用户的这个问题，推测用户可能会继续询问的下一个问题，给出三个可能" +
            "assistant：支持向量机的运行机制和原理是什么？ 在实际应用中，支持向量机主要应用于哪些领域或场景？ 如何利用支持向量机来解决实际问题，例如分类、回归或预测任务？";
    private final String nq_prompt = "请你根据用户的这个问题，推测用户可能会继续询问的下一个问题，给出三个可能。 assistant：";

    private final String tittle_CoT = "user：'如何证明 (p → q) → ((r → p) → (r → q)) 是一个重言式？' 请你根据用户的这个问题，总结出这段对话的标题。"+
            "assistant：逻辑与命题演算的讨论"+
            "user：'如何用Python随机生成20个1到1000之间的整数？' 请你根据用户的这个问题，总结出这段对话的标题。"+
            "assistant：Python编程问题：随机数生成"+
            "user：'我正在修改JSON文件，如何递增每条记录的id字段？' 请你根据用户的这个问题，总结出这段对话的标题。"+
            "assistant：JSON数据处理与格式转换";
    private final String tittle_prompt = "请你根据用户的这个问题，总结出这段对话的标题。 assistant：";

    /**
     * 根据用户的问题推测用户可能感兴趣的下一个问题，返回一个具有三个问题的列表，每次调用耗时大概3-4S，建议与chat方法同步调用
     * @param user_question
     * @return List<String>
     */
    public List<String> predictNextQuestions(String user_question) {
        ChatResponse resp = qianfan.chatCompletion()
                .model("Yi-34B-Chat")
                .addMessage("user",nq_CoT + "user：'" + user_question + "'" + nq_prompt)
                .execute();
        String ans = resp.getResult();

        return ans.lines()
                .map(String::trim) // 删除每个元素的前后空格
                .filter(s -> !s.isEmpty()) // 过滤掉空字符串
                .collect(Collectors.toList()); // 收集成列表
    }

    public String predictTittle(String user_question) {
        ChatResponse resp = qianfan.chatCompletion()
                .model("Yi-34B-Chat")
                .addMessage("user",tittle_CoT + "user：'" + user_question + "'" + tittle_prompt)
                .execute();
        String ans = resp.getResult();
        return ans;
    }

//调用示例
//    public static void main(String[] args) {
//        String q = "Python怎么生成随机数？";
//        uitl u = new uitl();
//        List<String> l = u.predictNextQuestions(q);
//        for (String s : l) {
//            System.out.println(s);
//        }
//        String t = u.predictTittle(q);
//        System.out.println(t);
//    }
}
