package org.bsc.langgraph4j;

import lombok.Value;
import lombok.experimental.Accessors;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.action.AsyncNodeActionWithConfig;
import org.bsc.langgraph4j.state.AgentState;

import java.util.Objects;

/**
 * Represents a node in a graph with a unique identifier and an associated action.
 *
 * @param <State> the type of the state associated with the node
 */
@Value
@Accessors(fluent = true)
class Node<State extends AgentState> {

    
    /**
     * The unique identifier for the node.
     */
    String id;

    /**
     * The action to be performed asynchronously by the node.
     */
    AsyncNodeAction<State> action;

    AsyncNodeActionWithConfig<State> actionWithConfig;

    public Node( String id ) {
        this.id = id;
        this.action = null;
        this.actionWithConfig = null;

    }
    public Node( String id, AsyncNodeAction<State> action ) {
        this.id = id;
        this.action = action;
        this.actionWithConfig = null;
    }
    public Node( String id, AsyncNodeActionWithConfig<State> actionWithConfig ) {
        this.id = id;
        this.action = null;
        this.actionWithConfig = actionWithConfig;

    }
    /**
     * Checks if this node is equal to another object.
     *
     * @param o the object to compare with
     * @return true if this node is equal to the specified object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(id, node.id);
    }

    /**
     * Returns the hash code value for this node.
     *
     * @return the hash code value for this node
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
