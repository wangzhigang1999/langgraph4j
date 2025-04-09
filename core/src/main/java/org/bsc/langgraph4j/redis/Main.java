package org.bsc.langgraph4j.redis;

import org.bsc.langgraph4j.RunnableConfig;
import org.bsc.langgraph4j.checkpoint.Checkpoint;
import redis.clients.jedis.Jedis;

import java.util.*;

public class Main {

    record Person(String name, int age) {
    }

    public static void main(String[] args) throws Exception {
        Jedis jedis = new Jedis("k8s.personai.cn", 30566);

        // use db 2
        jedis.select(2);

        // 创建检查点序列化器
        CheckpointSerializer checkpointSerializer = new CheckpointSerializer();

        // 创建 Redis 存储类
        RedisSaver saver = new RedisSaver(jedis, checkpointSerializer);

        // 创建一个 RunnableConfig
        RunnableConfig config = RunnableConfig.builder()
                .sessionId(UUID.randomUUID().toString())
                .build();

        HashMap<String, Object> hashMap = getHashMap();

        // 创建一个 Checkpoint
        Checkpoint checkpoint = Checkpoint.builder()
                .id("checkpoint-1")
                .nodeId("node-1")
                .nextNodeId("node-2")
                .state(hashMap)
                .build();

        // 保存检查点
        saver.put(config, checkpoint);

        // 获取检查点
        Optional<Checkpoint> retrievedCheckpoint = saver.get(config);
        retrievedCheckpoint.ifPresent(cp -> {
            System.out.println("Retrieved checkpoint ID: " + cp.getId());
            System.out.println("Retrieved checkpoint state: " + cp.getState());

            // Check if the Person object was correctly deserialized
            Map<String, Object> state = cp.getState();
            System.out.println("Person from state: " + state.get("person"));
            System.out.println("List from state: " + state.get("list"));

        });

        // 清除检查点
        // saver.clear(config);

        // 关闭 Jedis 连接
        jedis.close();
    }

    private static HashMap<String, Object> getHashMap() {
        HashMap<String, Object> hashMap = new HashMap<>();

        // 添加一些基本数据类型的包装类实例
        hashMap.put("number", 123);  // Integer
        hashMap.put("decimal", 123.45);  // Double
        hashMap.put("character", 'A');  // Character
        // 添加字符串
        hashMap.put("string", "Hello World");
        // 添加布尔值
        hashMap.put("boolean", true);
        // 添加自定义对象
        Person person = new Person("Alice", 30);
        hashMap.put("person", person);
        // 添加集合
        List<String> list = Arrays.asList("one", "two", "three");
        hashMap.put("list", list);
        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3));
        hashMap.put("set", set);
        // 添加数组
        String[] array = {"apple", "banana", "cherry"};
        hashMap.put("array", array);
        return hashMap;
    }
}
