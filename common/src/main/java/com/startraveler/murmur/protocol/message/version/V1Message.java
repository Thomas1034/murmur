package com.startraveler.murmur.protocol.message.version;


import com.startraveler.murmur.protocol.message.Message;
import com.startraveler.murmur.protocol.message.MessageBody;
import com.startraveler.murmur.registry.CodecRegistry;
import com.startraveler.murmur.protocol.Versions;

public record V1Message<T extends MessageBody>(T body, String mod) implements Message<T> {

    @Override
    public CodecRegistry.CodecRegistryEntry<? extends Message<? extends MessageBody>> getVersion() {
        return Versions.VERSION_1;
    }
}
