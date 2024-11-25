package com.aibotplatform.llm;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import reactor.core.publisher.Flux;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class CalculatorBot implements LLM {
    private static final String OPENAI_API_KEY = "sk-6hMxxGzo2ZT6WzKXBa9cB82d964e4cAe9eE0F95d70C1Ba0e";
    private final CalculatorAI calculatorAI;

    public CalculatorBot() {
        // 创建 OpenAI GPT-4 模型实例
        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(OPENAI_API_KEY)
                .baseUrl("https://xiaoai.plus/v1")
                .modelName(GPT_4_O_MINI)
                .temperature(0.0)
                .build();

        // 创建AI服务
        calculatorAI = AiServices.builder(CalculatorAI.class)
                .streamingChatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .tools(new CalculatorTools())
                .build();
    }

    @Override
    public Flux<String> chat(String input) {
        try {
            return calculatorAI.calculate(input);
        } catch (Exception e) {
            return Flux.just("计算出错: " + e.getMessage());
        }
    }
}

interface CalculatorAI {
    @SystemMessage("你是一个专业的计算器，可以处理各种数学计算。" +
            "请根据用户输入选择合适的计算工具来执行计算。" +
            "对于无效输入，请给出友好的提示。")
    Flux<String> calculate(String expression);
}

class CalculatorTools {

    @Tool("处理基础数学运算，包括加减乘除和幂运算")
    public double calculate(@P("第一个数字") double num1, @P("第二个数字") double num2, @P("运算符，支持 'add' 或 '+'（加），'subtract' 或 '-'（减），'multiply' 或 '*'（乘），'divide' 或 '/'（除），'power' 或 '^'（幂运算）") String operation) {
        return switch (operation.toLowerCase()) {
            case "add", "+" -> num1 + num2;
            case "subtract", "-" -> num1 - num2;
            case "multiply", "*" -> num1 * num2;
            case "divide", "/" -> {
                if (num2 == 0) {
                    throw new IllegalArgumentException("除数不能为0");
                }
                yield num1 / num2;
            }
            case "power", "^" -> Math.pow(num1, num2);
            default -> throw new IllegalArgumentException("不支持的运算符: " + operation);
        };
    }

    @Tool("计算三角函数，参数为角度值")
    public double trigonometric(@P("角度值，以度数表示") double angle, @P("三角函数类型，支持 'sin'（正弦），'cos'（余弦），'tan'（正切）") String function) {
        // 将角度转换为弧度
        double radians = Math.toRadians(angle);
        return switch (function.toLowerCase()) {
            case "sin" -> Math.sin(radians);
            case "cos" -> Math.cos(radians);
            case "tan" -> Math.tan(radians);
            default -> throw new IllegalArgumentException("不支持的三角函数: " + function);
        };
    }

    @Tool("计算对数，包括自然对数(ln)和常用对数(log)")
    public double logarithm(@P("正数值") double value, @P("对数类型，支持 'ln'（自然对数）或 'log'（常用对数）") String type) {
        if (value <= 0) {
            throw new IllegalArgumentException("对数函数的参数必须为正数");
        }
        return switch (type.toLowerCase()) {
            case "ln" -> Math.log(value);
            case "log" -> Math.log10(value);
            default -> throw new IllegalArgumentException("不支持的对数类型: " + type);
        };
    }

    @Tool("计算平方根")
    public double sqrt(@P("非负数值") double value) {
        if (value < 0) {
            throw new IllegalArgumentException("不能计算负数的平方根");
        }
        return Math.sqrt(value);
    }

    @Tool("计算e的指数幂")
    public double exp(@P("指数值") double power) {
        return Math.exp(power);
    }

    @Tool("计算绝对值")
    public double abs(@P("任意数值") double value) {
        return Math.abs(value);
    }

}

