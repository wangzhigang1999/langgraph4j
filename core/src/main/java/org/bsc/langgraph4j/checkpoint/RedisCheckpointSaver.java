package org.bsc.langgraph4j.checkpoint;

import org.bsc.langgraph4j.RunnableConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class RedisCheckpointSaver implements BaseCheckpointSaver {
    private final String redisHost = "k8s.personai.cn";
    private final int redisPort = 30566;

    private final JedisPoolConfig poolConfig = new JedisPoolConfig();
    JedisPool jedisPool = new JedisPool(poolConfig, redisHost, redisPort, 2000);

    @Override
    public Collection<Checkpoint> list(RunnableConfig config) {
        Optional<String> sessionIdOpt = config.sessionId();
        if (sessionIdOpt.isEmpty()) {
            return Collections.emptyList();
        }

        String sessionId = sessionIdOpt.get();
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(sessionId.getBytes(), 0, -1).stream()
                    .map(this::deserializeCheckpoint)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Optional<Checkpoint> get(RunnableConfig config) {
        Optional<String> sessionIdOpt = config.sessionId();
        if (sessionIdOpt.isEmpty()) {
            return Optional.empty();
        }

        String sessionId = sessionIdOpt.get();
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] latestCheckpointBytes = jedis.lindex(sessionId.getBytes(), -1);
            return deserializeCheckpoint(latestCheckpointBytes);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public RunnableConfig put(RunnableConfig config, Checkpoint checkpoint) throws Exception {
        Optional<String> sessionIdOpt = config.sessionId();
        if (sessionIdOpt.isEmpty()) {
            throw new IllegalArgumentException("Session ID is required");
        }

        String sessionId = sessionIdOpt.get();
        try (Jedis jedis = jedisPool.getResource()) {
            byte[] serializedCheckpoint = serializeCheckpoint(checkpoint);
            jedis.rpush(sessionId.getBytes(), serializedCheckpoint);
        }
        return config;
    }

    // Helper method to serialize Checkpoint to byte[]
    private byte[] serializeCheckpoint(Checkpoint checkpoint) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(checkpoint);
            return bos.toByteArray();
        }
    }

    // Helper method to deserialize byte[] to Checkpoint
    private Optional<Checkpoint> deserializeCheckpoint(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return Optional.empty();
        }

        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return Optional.of((Checkpoint) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
