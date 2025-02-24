package org.bsc.langgraph4j.agentexecutor.actions;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessageType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.NodeAction;
import org.bsc.langgraph4j.agentexecutor.Agent;
import org.bsc.langgraph4j.agentexecutor.AgentExecutor;
import org.bsc.langgraph4j.langchain4j.tool.ToolNode;

import java.util.Map;
import java.util.Optional;

/**
 * The ExecuteTools class implements the NodeAction interface for handling 
 * actions related to executing tools within an agent's context.
 */
@Slf4j
public class ExecuteTools implements NodeAction<AgentExecutor.State> {

    /**
     * The agent associated with this execution tool.
     */
    final Agent agent;

    /**
     * The tool node that will be executed.
     */
    final ToolNode toolNode;

    /**
     * Constructs an ExecuteTools instance with the specified agent and tool node.
     *
     * @param agent the agent to be associated with this execution tool, must not be null
     * @param toolNode the tool node to be executed, must not be null
     */
    public ExecuteTools(@NonNull Agent agent, @NonNull ToolNode toolNode) {
        this.agent = agent;
        this.toolNode = toolNode;
    }

    /**
     * Applies the tool execution logic based on the provided agent state.
     *
     * @param state the current state of the agent executor
     * @return a map containing the intermediate steps of the execution
     * @throws IllegalArgumentException if no agent outcome is provided
     * @throws IllegalStateException if no action or tool is found for execution
     */
    @Override
    public Map<String,Object> apply(AgentExecutor.State state )  {
        log.trace( "executeTools" );

        var toolExecutionRequests = state.lastMessage()
                .filter( m -> ChatMessageType.AI==m.type() )
                .map( m -> (AiMessage)m )
                .filter(AiMessage::hasToolExecutionRequests)
                .map(AiMessage::toolExecutionRequests)
                .orElseThrow(() -> new IllegalArgumentException("no tool execution request found!"));

        var result = toolExecutionRequests.stream()
                        .map(toolNode::execute)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();

        return Map.of("messages", result );

    }

}
