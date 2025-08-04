package com.startraveler.murmur.protocol.message.version;


import com.startraveler.murmur.protocol.message.Message;
import com.startraveler.murmur.protocol.message.MessageBody;
import com.startraveler.murmur.registry.CodecRegistry;
import com.startraveler.murmur.protocol.Versions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record V1Message<T extends MessageBody>(@NotNull T body, @NotNull String mod, @Nullable UUID sender) implements Message<T> {

    @Override
    public CodecRegistry.CodecRegistryEntry<? extends Message<? extends MessageBody>> getVersion() {
        return Versions.VERSION_1;
    }
}
