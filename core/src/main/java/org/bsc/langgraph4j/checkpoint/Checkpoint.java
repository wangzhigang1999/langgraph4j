package org.bsc.langgraph4j.checkpoint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;

/**
 * Represents a checkpoint of an agent state.
 * <p>
 * The checkpoint is an immutable object that holds an {@link AgentState}
 * and a {@code String} that represents the next state.
 * <p>
 * The checkpoint is serializable and can be persisted and restored.
 *
 * @see AgentState
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Checkpoint implements Serializable {

    private String id = UUID.randomUUID().toString();
    private Map<String, Object> state = null;
    private String nodeId = null;
    private String nextNodeId = null;

    public Checkpoint(Checkpoint checkpoint) {
        this.id = checkpoint.id;
        this.state = checkpoint.state;
        this.nodeId = checkpoint.nodeId;
        this.nextNodeId = checkpoint.nextNodeId;
    }

    public Checkpoint updateState(Map<String, Object> values, Map<String, Channel<?>> channels) {
        Checkpoint result = new Checkpoint(this);
        result.state = AgentState.updateState(state, values, channels);
        return result;
    }

    @Override
    public String toString() {
        return format("Checkpoint{ id=%s, nodeId=%s, nextNodeId=%s, state=%s }",
                id,
                nodeId,
                nextNodeId,
                state
        );
    }
}
