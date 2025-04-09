package org.bsc.langgraph4j.checkpoint;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileCheckpointSaver {

    private final String baseDir;

    public FileCheckpointSaver(String baseDir) {
        this.baseDir = baseDir;
        ensureDirectoryExists();
    }

    private void ensureDirectoryExists() {
        try {
            Files.createDirectories(Paths.get(baseDir));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory: " + baseDir, e);
        }
    }

    /**
     * 保存 Checkpoint 到文件（文件名格式: {sessionId}_{timestamp}.json）
     */
    public void save(String sessionId, Checkpoint checkpoint) throws IOException {
        String fileName = String.format("%s_%d.json", sessionId, System.currentTimeMillis());
        String filePath = Paths.get(baseDir, fileName).toString();
        CheckpointJsonSerializer.serializeToFile(checkpoint, filePath);
    }

    /**
     * 列出某个会话的所有 Checkpoint 文件
     */
    public List<Checkpoint> list(String sessionId) {
        try {
            return Files.list(Paths.get(baseDir))
                    .filter(path -> path.getFileName().toString().startsWith(sessionId + "_"))
                    .sorted() // 按文件名排序（时间戳自然有序）
                    .map(path -> {
                        try {
                            return CheckpointJsonSerializer.deserializeFromFile(path.toString());
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    /**
     * 获取某个会话的最新 Checkpoint
     */
    public Optional<Checkpoint> getLatest(String sessionId) {
        List<Checkpoint> checkpoints = list(sessionId);
        return checkpoints.isEmpty() ? Optional.empty() : Optional.of(checkpoints.get(checkpoints.size() - 1));
    }
}
