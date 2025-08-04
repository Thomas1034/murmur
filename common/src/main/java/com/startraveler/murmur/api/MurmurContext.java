package com.startraveler.murmur.api;

import com.google.gson.JsonElement;
import com.mojang.serialization.MapCodec;
import com.startraveler.murmur.event.EventBus;
import com.startraveler.murmur.protocol.MessageFormat;
import com.startraveler.murmur.protocol.ProtocolCodecRegistries;
import com.startraveler.murmur.protocol.message.Message;
import com.startraveler.murmur.protocol.message.MessageBody;
import com.startraveler.murmur.protocol.message.version.V1Message;
import com.startraveler.murmur.registry.CodecRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MurmurContext<T> {

    public static final List<MurmurContext<?>> CONTEXTS = new ArrayList<>();

    protected final MessageFormat<T> messageFormat;
    protected final TriFunction<MessageBody, UUID, Object[], Message<? extends MessageBody>> messageSupplier;
    protected final String mod;
    protected final EventBus<MessageBody> eventBus;

    public MurmurContext(String mod, MessageFormat<T> messageFormat, TriFunction<MessageBody, UUID, Object[], Message<? extends MessageBody>> messageSupplier, EventBus<MessageBody> eventBus) {
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
                (body, id, arr) -> new V1Message<>(body, mod, id),
                new EventBus<>()
        );
    }

    public <S extends MessageBody> CodecRegistry.CodecRegistryEntry<S> registerMessageType(@NotNull ResourceLocation location, @NotNull MapCodec<S> codec) {
        return ProtocolCodecRegistries.MESSAGE_TYPES.register(location, codec);
    }

    public void registerMessageHandler(@NotNull CodecRegistry.CodecRegistryEntry<? extends MessageBody> type, @NotNull BiConsumer<MessageBody, Object[]> handler) {
        this.eventBus.register(type, handler);
    }

    public void registerMessageHandler(@NotNull CodecRegistry.CodecRegistryEntry<? extends MessageBody> type, @NotNull Consumer<MessageBody> handler) {
        this.registerMessageHandler(type, (messageBody, payload) -> handler.accept(messageBody));
    }

    @SuppressWarnings("unchecked")
    public <S extends MessageBody> CodecRegistry.CodecRegistryEntry<S> registerMessageType(@NotNull ResourceLocation location, @NotNull MapCodec<S> codec, @NotNull BiConsumer<S, Object[]> handler) {
        CodecRegistry.CodecRegistryEntry<S> type = this.registerMessageType(location, codec);
        this.registerMessageHandler(
                type,
                (MessageBody rawBody, Object[] payload) -> handler.accept((S) rawBody, payload)
        );
        return type;
    }

    @SuppressWarnings("unchecked")
    public <S extends MessageBody> CodecRegistry.CodecRegistryEntry<S> registerMessageType(@NotNull ResourceLocation location, @NotNull MapCodec<S> codec, @NotNull Consumer<S> handler) {
        CodecRegistry.CodecRegistryEntry<S> type = this.registerMessageType(location, codec);
        this.registerMessageHandler(type, (MessageBody rawBody, Object[] payload) -> handler.accept((S) rawBody));
        return type;
    }

    public void sendMessage(@NotNull MessageBody body, @NotNull Object... messageContext) {
        // TODO
        LocalPlayer player = Minecraft.getInstance().player;
        String message = this.makeMessage(body, player == null ? null : player.getUUID(), messageContext);



    }

    public String makeMessage(@NotNull MessageBody body, @Nullable UUID id, @NotNull Object... messageContext) {
        Objects.requireNonNull(body);
        Objects.requireNonNull(messageContext);
        Message<?> message = this.messageSupplier.apply(body, id, messageContext);
        return this.messageFormat.encode(message);
    }

    @Nullable
    protected Message<? extends MessageBody> parseMessage(@NotNull String message) {
        return this.messageFormat.decode(message);
    }

    public boolean receiveMessage(@NotNull String message) {
        Message<? extends MessageBody> parsedMessage = this.parseMessage(message);
        if (parsedMessage != null) {
            MessageBody body = parsedMessage.body();
            if (body != null) {
                this.eventBus.fire(body.type(), body, parsedMessage.sender());
                return true;
            }
        }
        return false;
    }
}
