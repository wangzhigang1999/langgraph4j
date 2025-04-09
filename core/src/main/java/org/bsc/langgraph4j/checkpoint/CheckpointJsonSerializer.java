package org.bsc.langgraph4j.checkpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

public class CheckpointJsonSerializer {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT); // 美化输出格式

    /**
     * 序列化 Checkpoint 到 JSON 文件
     */
    public static void serializeToFile(Checkpoint checkpoint, String filePath) throws IOException {
        objectMapper.writeValue(new File(filePath), checkpoint);
    }

    /**
     * 从 JSON 文件反序列化 Checkpoint
     */
    public static Checkpoint deserializeFromFile(String filePath) throws IOException {
        return objectMapper.readValue(new File(filePath), Checkpoint.class);
    }
}
