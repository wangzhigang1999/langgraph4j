package org.bsc.langgraph4j.checkpoint;

import org.bsc.langgraph4j.RunnableConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 真实操作 Redis 的集成测试（需确保 Redis 可连接）
 */
public class RedisCheckpointSaverIntegrationTest {

    private static final String TEST_SESSION_ID = "test_session_123";
    private static final String REDIS_HOST = "k8s.personai.cn";
    private static final int REDIS_PORT = 30566;

    private RedisCheckpointSaver checkpointSaver;
    private JedisPool jedisPool;

    @BeforeEach
    void setUp() throws Exception {
        // 初始化 Redis 连接池和测试对象
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(poolConfig, REDIS_HOST, REDIS_PORT, 2000);
        jedisPool.preparePool();
        checkpointSaver = new RedisCheckpointSaver();
    }

    @AfterEach
    void tearDown() {
//        // 清理测试数据
//        try (Jedis jedis = jedisPool.getResource()) {
//            jedis.del(TEST_SESSION_ID.getBytes());
//        }
        jedisPool.close();
    }

    @Test
    void testPutAndGetLatestCheckpoint() throws Exception {
        // 准备测试数据
        RunnableConfig config = RunnableConfig.builder().sessionId(TEST_SESSION_ID).build();
        Checkpoint checkpoint1 = Checkpoint.builder().state(Map.of("key1", "value1")).nodeId("node1").nextNodeId("node2").build();

        Checkpoint checkpoint2 = Checkpoint.builder().state(Map.of("key2", 123)).nodeId("node3").nextNodeId("node4").build();

        // 写入 Redis
        checkpointSaver.put(config, checkpoint1);
        checkpointSaver.put(config, checkpoint2);

        // 验证最新 Checkpoint
        Optional<Checkpoint> latest = checkpointSaver.get(config);
        assertTrue(latest.isPresent());
        assertEquals("node3", latest.get().getNodeId()); // 应为最后写入的 checkpoint2
    }

    @Test
    void testListAllCheckpoints() throws Exception {
        RunnableConfig config = RunnableConfig.builder().sessionId(TEST_SESSION_ID).build();
        Checkpoint checkpoint1 = Checkpoint.builder().nodeId("node1").build();
        Checkpoint checkpoint2 = Checkpoint.builder().nodeId("node2").build();

        // 写入两个 Checkpoint
        checkpointSaver.put(config, checkpoint1);
        checkpointSaver.put(config, checkpoint2);

        // 获取全部
        Collection<Checkpoint> checkpoints = checkpointSaver.list(config);
        assertEquals(2, checkpoints.size());

        // 验证顺序（List 按插入顺序存储）
        Checkpoint[] array = checkpoints.toArray(new Checkpoint[0]);
        assertEquals("node1", array[0].getNodeId()); // 先写入的在前
        assertEquals("node2", array[1].getNodeId()); // 后写入的在后
    }

    @Test
    void testEmptySession() {
        RunnableConfig config = RunnableConfig.builder().sessionId("non_existent_session").build();
        // 不存在的会话应返回空
        assertTrue(checkpointSaver.list(config).isEmpty());
        assertTrue(checkpointSaver.get(config).isEmpty());
    }

    @Test
    void testBinaryDataInRedis() throws Exception {
        RunnableConfig config = RunnableConfig.builder().sessionId(TEST_SESSION_ID).build();
        Checkpoint checkpoint = Checkpoint.builder().nodeId("test_node").build();

        // 写入 Redis
        checkpointSaver.put(config, checkpoint);

        // 直接通过 Jedis 验证二进制数据
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] binaryData = jedis.lindex(TEST_SESSION_ID.getBytes(), 0);
            assertNotNull(binaryData);
            assertTrue(binaryData.length > 0); // 确保非空二进制
        }
    }
}
