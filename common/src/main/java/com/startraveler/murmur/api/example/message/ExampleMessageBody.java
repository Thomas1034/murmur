package com.startraveler.murmur.api.example.message;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.startraveler.murmur.protocol.message.MessageBody;
import com.startraveler.murmur.registry.CodecRegistry;

public record ExampleMessageBody(int x, int y, int z) implements MessageBody {
    public static final MapCodec<ExampleMessageBody> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("x").forGetter(ExampleMessageBody::x),
            Codec.INT.fieldOf("y").forGetter(ExampleMessageBody::y),
            Codec.INT.fieldOf("z").forGetter(ExampleMessageBody::z)
    ).apply(instance, ExampleMessageBody::new));

    @Override
    public CodecRegistry.CodecRegistryEntry<? extends MessageBody> type() {
        return ExampleMessageTypes.EXAMPLE_MESSAGE_TYPE;
    }
}
