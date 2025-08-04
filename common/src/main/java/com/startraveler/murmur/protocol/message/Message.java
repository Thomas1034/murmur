package com.startraveler.murmur.protocol.message;

import com.startraveler.murmur.registry.CodecRegistry;

import java.util.UUID;

public interface Message<T extends MessageBody> {

    CodecRegistry.CodecRegistryEntry<? extends Message<? extends MessageBody>> getVersion();

    T body();

    String mod();

    UUID sender();

}
