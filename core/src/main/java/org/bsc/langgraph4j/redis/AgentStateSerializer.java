package org.bsc.langgraph4j.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bsc.langgraph4j.serializer.Serializer;
import org.bsc.langgraph4j.state.AgentState;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class AgentStateSerializer<T extends AgentState> implements Serializer<T> {
    private final Gson gson;
    private final Class<T> stateClass;

    public AgentStateSerializer(Class<T> stateClass) {
        this.gson = new GsonBuilder().serializeNulls().create();
        this.stateClass = stateClass;
    }

    @Override
    public void write(T state, ObjectOutput out) throws IOException {
        String json = gson.toJson(state);
        out.writeUTF(json);
    }

    @Override
    public T read(ObjectInput in) throws IOException, ClassNotFoundException {
        String json = in.readUTF();
        return gson.fromJson(json, stateClass);
    }
}
