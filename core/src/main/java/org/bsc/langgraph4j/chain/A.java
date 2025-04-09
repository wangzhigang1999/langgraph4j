package org.bsc.langgraph4j.chain;

import dev.langchain4j.chain.Chain;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.HashMap;
import java.util.Map;

import static org.bsc.langgraph4j.chain.CustomerSupportWorkflow.simulateDataRetrieval;

public class A {

    public static void chainBasedWorkflow() {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();

        // 定义分类提示模板
        PromptTemplate classifierPrompt = PromptTemplate.from(
                "将以下客户查询分类为：技术问题、账单问题、产品咨询或其他。只返回分类名称。\n\n" +
                        "客户查询: {{query}}\n\n" +
                        "分类:"
        );

        // 定义信息检索提示模板
        PromptTemplate retrieverPrompt = PromptTemplate.from(
                "根据以下客户问题和问题分类，确定需要检索的信息类型。\n\n" +
                        "分类: {{category}}\n" +
                        "客户查询: {{query}}\n\n" +
                        "需要检索的信息:"
        );

        // 定义响应生成提示模板
        PromptTemplate responsePrompt = PromptTemplate.from(
                "作为客户支持代表，使用以下信息生成友好专业的回复。\n\n" +
                        "客户查询: {{query}}\n" +
                        "问题分类: {{category}}\n" +
                        "相关信息: {{info}}\n\n" +
                        "回复:"
        );

        // 创建顺序工作流
        Chain<Map<String, String>> workflow = SequentialChain.builder()
                .chain(input -> {
                    String result = model.generate(classifierPrompt.apply(input)).content().text();
                    input.put("category", result.trim());
                    return input;
                })
                .chain(input -> {
                    Map<String, Object> vars = new HashMap<>(input);
                    String result = model.generate(retrieverPrompt.apply(vars)).content().text();
                    input.put("required_info", result.trim());

                    // 模拟数据检索
                    String customerId = "customer-123";
                    String retrievedInfo = simulateDataRetrieval(result.trim(), customerId);
                    input.put("info", retrievedInfo);
                    return input;
                })
                .chain(input -> {
                    Map<String, Object> vars = new HashMap<>(input);
                    String result = model.generate(responsePrompt.apply(vars)).content().text();
                    input.put("response", result.trim());
                    return input;
                })
                .build();

        // 执行工作流
        String customerQuery = "我的高级订阅每月费用是多少？我觉得我被多收费了。";
        Map<String, String> input = new HashMap<>();
        input.put("query", customerQuery);

        Map<String, String> result = workflow.execute(input);
        System.out.println("分类: " + result.get("category"));
        System.out.println("需要信息: " + result.get("required_info"));
        System.out.println("检索信息: " + result.get("info"));
        System.out.println("\n最终回复:\n" + result.get("response"));
    }

}