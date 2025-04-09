package org.bsc.langgraph4j.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bsc.langgraph4j.checkpoint.Checkpoint;
import org.bsc.langgraph4j.serializer.Serializer;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class CheckpointSerializer implements Serializer<Checkpoint> {
    private final Gson gson;

    public CheckpointSerializer() {
        this.gson = new GsonBuilder().serializeNulls().create();
    }

    @Override
    public void write(Checkpoint checkpoint, ObjectOutput out) throws IOException {
        String json = gson.toJson(checkpoint);
        out.writeUTF(json);
    }

    @Override
    public Checkpoint read(ObjectInput in) throws IOException, ClassNotFoundException {
        String json = in.readUTF();
        return gson.fromJson(json, Checkpoint.class);
    }

    public String toJson(Checkpoint checkpoint) {
        return gson.toJson(checkpoint);
    }

    public Checkpoint fromJson(String json) {
        return gson.fromJson(json, Checkpoint.class);
    }
}
