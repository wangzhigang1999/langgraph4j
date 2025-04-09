package org.bsc.langgraph4j.chain;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.DefaultChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;

import java.util.HashMap;
import java.util.Map;

public class CustomerSupportWorkflow {

    // 定义工作流中的各种服务接口
    interface TicketClassifier {
        @SystemMessage("你是一名客户支持分类专家，负责将客户请求分类为：技术问题、账单问题、产品咨询或其他。仅返回分类名称，不要有其他文字。")
        String classify(String customerQuery);
    }

    interface InformationRetriever {
        @SystemMessage("根据客户问题和问题分类，确定需要检索的信息类型。返回格式为'需要检索：XXX'。")
        String determineInformationNeeded(String category, String customerQuery);
    }

    interface ResponseGenerator {
        @SystemMessage("你是一名客户支持代表。使用提供的信息，生成一个友好、专业的回复给客户。")
        String generateResponse(@MemoryId String memoryId, String category, String requiredInfo, String customerQuery);
    }

    public static void main(String[] args) {
        // 初始化OpenAI模型
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();

        // 创建各个服务实例
        TicketClassifier classifier = AiServices.create(TicketClassifier.class, model);
        InformationRetriever retriever = AiServices.create(InformationRetriever.class, model);
        Map<String, ChatMemory> memories = new HashMap<>();
        
        ResponseGenerator responseGenerator = AiServices.builder(ResponseGenerator.class)
                .chatLanguageModel(model)
                .chatMemoryProvider(new ChatMemoryProvider() {
                    @Override
                    public ChatMemory get(Object o) {
                        return memories.computeIfAbsent(o.toString(), k -> {
                            // 使用消息窗口来存储聊天记录
                            return MessageWindowChatMemory.withMaxMessages(5);
                        });
                    }
                })
                .build();

        // 示例客户查询
        String customerQuery = "我的高级订阅每月费用是多少？我觉得我被多收费了。";
        String customerId = "customer-123";

        // 执行工作流
        try {
            // 步骤1：分类客户请求
            String category = classifier.classify(customerQuery);
            System.out.println("请求分类: " + category);

            // 步骤2：确定需要的信息
            String requiredInfo = retriever.determineInformationNeeded(category, customerQuery);
            System.out.println("需要检索: " + requiredInfo);

            // 模拟从数据库检索信息
            String retrievedInfo = simulateDataRetrieval(requiredInfo, customerId);
            System.out.println("检索到信息: " + retrievedInfo);

            // 步骤3：生成响应
            String response = responseGenerator.generateResponse(customerId, category, retrievedInfo, customerQuery);
            System.out.println("\n最终响应: \n" + response);
        } catch (Exception e) {
            System.err.println("工作流执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 模拟从数据库检索信息
    private static String simulateDataRetrieval(String requiredInfo, String customerId) {
        // 在实际应用中，这里会连接到真实数据库或API
        if (requiredInfo.contains("订阅") || requiredInfo.contains("账单")) {
            return "客户ID: " + customerId + 
                   ", 订阅类型: 高级会员, 月费: $19.99, 上次账单日期: 2023-04-15, 实际收费: $19.99";
        } else if (requiredInfo.contains("技术问题")) {
            return "常见问题: 登录问题, 应用崩溃, 同步失败. 技术支持电话: 400-123-4567";
        } else {
            return "无相关信息";
        }
    }
}
