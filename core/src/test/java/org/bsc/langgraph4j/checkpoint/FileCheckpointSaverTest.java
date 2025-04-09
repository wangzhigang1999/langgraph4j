package org.bsc.langgraph4j.checkpoint;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

class FileCheckpointSaverTest {

    private static final String TEST_DIR = "target/checkpoint_test";
    private static final String TEST_SESSION = "test_session_001";

    private FileCheckpointSaver fileSaver;

    @BeforeEach
    void setUp() {
        // 每次测试前清空目录
        deleteTestFiles();
        fileSaver = new FileCheckpointSaver(TEST_DIR);
    }

    @AfterEach
    void tearDown() {
        // 测试后清理文件
//        deleteTestFiles();
    }

    private void deleteTestFiles() {
        try {
            Files.list(Paths.get(TEST_DIR))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {}
                    });
        } catch (IOException ignored) {}
    }

    @Test
    void testSaveAndLoadCheckpoint() throws IOException {
        // 准备测试数据
        Checkpoint checkpoint = Checkpoint.builder()
                .nodeId("node1")
                .state(Map.of("key", "value"))
                .build();

        // 保存到文件
        fileSaver.save(TEST_SESSION, checkpoint);

        // 验证文件存在
        Path filePath = Files.list(Paths.get(TEST_DIR)).findFirst().orElseThrow();
        assertTrue(Files.exists(filePath));
        assertTrue(filePath.toString().endsWith(".json"));

        // 反序列化验证内容
        Checkpoint loaded = CheckpointJsonSerializer.deserializeFromFile(filePath.toString());
        assertEquals("node1", loaded.getNodeId());
        assertEquals("value", loaded.getState().get("key"));
    }

    @Test
    void testListAndGetLatest() throws IOException, InterruptedException {
        // 写入多个 Checkpoint
        fileSaver.save(TEST_SESSION, Checkpoint.builder().nodeId("node1").build());
        Thread.sleep(10); // 确保时间戳不同
        fileSaver.save(TEST_SESSION, Checkpoint.builder().nodeId("node2").build());

        // 验证 list()
        assertEquals(2, fileSaver.list(TEST_SESSION).size());

        // 验证 getLatest()
        Optional<Checkpoint> latest = fileSaver.getLatest(TEST_SESSION);
        assertTrue(latest.isPresent());
        assertEquals("node2", latest.get().getNodeId()); // 最后写入的应为最新
    }
}
