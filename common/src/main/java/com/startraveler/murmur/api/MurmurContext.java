package com.startraveler.murmur.api;

import com.google.gson.JsonElement;
import com.mojang.serialization.MapCodec;
import com.startraveler.murmur.Constants;
import com.startraveler.murmur.event.EventBus;
import com.startraveler.murmur.protocol.MessageFormat;
import com.startraveler.murmur.protocol.ProtocolCodecRegistries;
import com.startraveler.murmur.protocol.message.Message;
import com.startraveler.murmur.protocol.message.MessageBody;
import com.startraveler.murmur.protocol.message.version.V1Message;
import com.startraveler.murmur.registry.CodecRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class MurmurContext<T> {

    public static final List<MurmurContext<?>> CONTEXTS = new ArrayList<>();

    protected final MessageFormat<T> messageFormat;
    protected final BiFunction<MessageBody, Object[], Message<? extends MessageBody>> messageSupplier;
    protected final String mod;
    protected final EventBus<MessageBody> eventBus;

    public MurmurContext(String mod, MessageFormat<T> messageFormat, BiFunction<MessageBody, Object[], Message<? extends MessageBody>> messageSupplier, EventBus<MessageBody> eventBus) {
        this.messageFormat = messageFormat;
        this.messageSupplier = messageSupplier;
        this.mod = mod;
        this.eventBus = eventBus;
        CONTEXTS.add(this);
    }

    public static MurmurContext<JsonElement> basic(String mod) {
        return new MurmurContext<>(
                mod,
                MessageFormat.DEFAULT_FORMAT,
                (body, arr) -> new V1Message<>(body, mod),
                new EventBus<>()
        );
    }

    public <S extends MessageBody> CodecRegistry.CodecRegistryEntry<S> registerMessageType(@NotNull ResourceLocation location, @NotNull MapCodec<S> codec) {
        return ProtocolCodecRegistries.MESSAGE_TYPES.register(location, codec);
    }

    public void registerMessageHandler(@NotNull CodecRegistry.CodecRegistryEntry<? extends MessageBody> type, @NotNull Consumer<MessageBody> handler) {
        this.eventBus.register(type, handler);
    }

    @SuppressWarnings("unchecked")
    public <S extends MessageBody> CodecRegistry.CodecRegistryEntry<S> registerMessageType(@NotNull ResourceLocation location, @NotNull MapCodec<S> codec, @NotNull Consumer<S> handler) {
        CodecRegistry.CodecRegistryEntry<S> type = this.registerMessageType(location, codec);
        this.registerMessageHandler(type, (MessageBody rawBody) -> handler.accept((S) rawBody));
        return type;
    }

    public String makeMessage(@NotNull MessageBody body, @NotNull Object... messageContext) {
        Objects.requireNonNull(body);
        Objects.requireNonNull(messageContext);
        Message<?> message = this.messageSupplier.apply(body, messageContext);
        return this.messageFormat.encode(message);
    }

    @Nullable
    protected MessageBody parseMessage(@NotNull String message) {
        Message<? extends MessageBody> decodedMessage = this.messageFormat.decode(message);
        if (decodedMessage == null) {
            return null;
        }
        return decodedMessage.body();
    }

    public boolean receiveMessage(@NotNull String message) {
        MessageBody body = this.parseMessage(message);
        if (body != null) {
            this.eventBus.fire(body.type(), body);
            return true;
        }
        return false;
    }


}
