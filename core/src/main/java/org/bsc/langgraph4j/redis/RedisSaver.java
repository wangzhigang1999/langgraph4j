package org.bsc.langgraph4j.redis;

import org.bsc.langgraph4j.RunnableConfig;
import org.bsc.langgraph4j.checkpoint.BaseCheckpointSaver;
import org.bsc.langgraph4j.checkpoint.Checkpoint;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RedisSaver implements BaseCheckpointSaver {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RedisSaver.class);
    private final Jedis jedis;
    private final CheckpointSerializer serializer;

    public RedisSaver(Jedis jedis, CheckpointSerializer serializer) {
        this.jedis = jedis;
        this.serializer = serializer;
    }

    /**
     * Generate Redis key for a checkpoint
     */
    private String getCheckpointKey(RunnableConfig config) {
        return "checkpoint:" + config.sessionId().orElse("default");
    }

    /**
     * Generate Redis key for checkpoint list
     */
    private String getCheckpointListKey(RunnableConfig config) {
        return "checkpoint_list:" + config.sessionId().orElse("default");
    }

    @Override
    public Collection<Checkpoint> list(RunnableConfig config) {
        String listKey = getCheckpointListKey(config);
        List<String> checkpointKeys = jedis.lrange(listKey, 0, -1).stream()
                .map(String::valueOf)
                .toList();

        List<Checkpoint> checkpoints = new ArrayList<>();

        for (String key : checkpointKeys) {
            String jsonData = jedis.get(key);
            if (jsonData != null) {
                try {
                    Checkpoint checkpoint = serializer.fromJson(jsonData);
                    checkpoints.add(checkpoint);
                } catch (Exception e) {
                    log.error("Failed to deserialize checkpoint from key {}: {}", key, e.getMessage());
                }
            }
        }

        return checkpoints;
    }

    @Override
    public Optional<Checkpoint> get(RunnableConfig config) {
        String key = getCheckpointKey(config);
        String jsonData = jedis.get(key);

        if (jsonData == null) {
            return Optional.empty();
        }

        try {
            Checkpoint checkpoint = serializer.fromJson(jsonData);
            return Optional.of(checkpoint);
        } catch (Exception e) {
            log.error("Failed to deserialize checkpoint from key {}: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public RunnableConfig put(RunnableConfig config, Checkpoint checkpoint) throws Exception {
        String key = getCheckpointKey(config);
        String listKey = getCheckpointListKey(config);

        try {
            // Serialize the checkpoint to JSON
            String jsonData = serializer.toJson(checkpoint);

            // Store the checkpoint
            jedis.set(key, jsonData);

            // Add the key to the list of checkpoints for this session
            // We use a list to maintain order if needed for listing
            jedis.lpush(listKey, key);

            // Optional: Trim the list to only keep recent checkpoints
            jedis.ltrim(listKey, 0, 99); // Keep last 100 checkpoints max

            log.info("Checkpoint {} saved to Redis with key {}", checkpoint.getId(), key);
            return config;
        } catch (Exception e) {
            log.error("Failed to save checkpoint to Redis: {}", e.getMessage());
            throw new Exception("Failed to save checkpoint to Redis", e);
        }
    }

    /**
     * Clear all checkpoints for a specific session
     */
    public boolean clear(RunnableConfig config) {
        String key = getCheckpointKey(config);
        String listKey = getCheckpointListKey(config);

        try {
            // Get all checkpoint keys for this session
            List<String> keys = jedis.lrange(listKey, 0, -1);

            // Delete all checkpoint data
            for (String checkpointKey : keys) {
                jedis.del(checkpointKey);
            }

            // Delete the list itself
            jedis.del(listKey);

            // Delete the latest checkpoint
            jedis.del(key);

            return true;
        } catch (Exception e) {
            log.error("Failed to clear checkpoints: {}", e.getMessage());
            return false;
        }
    }
}
