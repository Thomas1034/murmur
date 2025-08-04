package com.startraveler.murmur.protocol;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.startraveler.murmur.Constants;
import com.startraveler.murmur.protocol.message.Message;
import com.startraveler.murmur.protocol.message.MessageBody;
import com.startraveler.murmur.protocol.message.version.V1Message;
import com.startraveler.murmur.registry.CodecRegistry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Function;

public class Versions {

    public static final String VERSION_DISPATCH_STRING = "version";

    public static final ResourceLocation V1_LOCATION = Constants.id("v1");

    public static final Codec<MessageBody> V1_BODY_CODEC = ProtocolCodecRegistries.MESSAGE_TYPES.forDispatch()
            .dispatch(body -> body.type().codec(), Function.identity());

    public static final MapCodec<V1Message<MessageBody>> V1_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            V1_BODY_CODEC.fieldOf("body").forGetter(V1Message::body),
            Codec.STRING.fieldOf("mod").forGetter(V1Message::mod),
            UUIDUtil.CODEC.optionalFieldOf("sender").forGetter((message) -> Optional.ofNullable(message.sender()))
    ).apply(instance, (body, mod, sender) -> new V1Message<>(body, mod, sender.orElse(null))));

    public static final CodecRegistry.CodecRegistryEntry<? extends Message<MessageBody>> VERSION_1 = ProtocolCodecRegistries.VERSIONS.register(
            V1_LOCATION,
            V1_CODEC
    );

    public static final Codec<Message<? extends MessageBody>> MESSAGE_CODEC = ProtocolCodecRegistries.VERSIONS.forDispatch()
            .dispatch(VERSION_DISPATCH_STRING, message -> message.getVersion().codec(), Function.identity());

    private Versions() {
    }
}
