package com.startraveler.murmur.protocol.message;

import com.startraveler.murmur.registry.CodecRegistry;

public interface MessageBody {
    CodecRegistry.CodecRegistryEntry<? extends MessageBody> type();
}
