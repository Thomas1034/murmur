package com.startraveler.murmur.protocol.message;

import com.startraveler.murmur.registry.CodecRegistry;

public interface Message<T extends MessageBody> {

    CodecRegistry.CodecRegistryEntry<? extends Message<? extends MessageBody>> getVersion();

    T body();

    String mod();

}
