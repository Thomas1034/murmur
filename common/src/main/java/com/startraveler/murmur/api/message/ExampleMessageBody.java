package com.startraveler.murmur.api.message;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.startraveler.murmur.registry.CodecRegistry;
import com.startraveler.murmur.protocol.message.MessageBody;

public record ExampleMessageBody(int a, int b) implements MessageBody {
    public static final MapCodec<ExampleMessageBody> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("a").forGetter(ExampleMessageBody::a),
            Codec.INT.fieldOf("b").forGetter(ExampleMessageBody::b)
    ).apply(instance, ExampleMessageBody::new));

    @Override
    public CodecRegistry.CodecRegistryEntry<? extends MessageBody> type() {
        return ExampleMessageTypes.EXAMPLE_MESSAGE_TYPE;
    }
}
