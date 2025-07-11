package com.startraveler.murmur.protocol;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.startraveler.murmur.Constants;
import com.startraveler.murmur.protocol.message.Message;
import com.startraveler.murmur.protocol.message.MessageBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class MessageFormat<T> {
    public static final String DEFAULT_MESSAGE_TAG = "@@" + Constants.MOD_ID + ":";
    public static final MessageFormat<JsonElement> DEFAULT_FORMAT = new MessageFormat<>(
            DEFAULT_MESSAGE_TAG,
            JsonOps.INSTANCE,
            JsonParser::parseString
    );
    protected final String packetTag;
    protected final DynamicOps<T> ops;
    protected final Function<String, T> parser;

    public MessageFormat(@NotNull String packetTag, @NotNull DynamicOps<T> ops, @NotNull Function<String, T> parser) {
        this.packetTag = packetTag;
        this.ops = ops;
        this.parser = parser;
    }

    public boolean isMessage(@NotNull String string) {
        return string.startsWith(this.packetTag);
    }

    @Nullable
    public String getBody(@NotNull String string) {
        int index = string.indexOf(this.packetTag);
        if (index == -1) {
            return null;
        }
        return string.substring(index + this.packetTag.length());
    }

    public String encode(@NotNull Message<? extends MessageBody> message) {
        return this.packetTag + Versions.MESSAGE_CODEC.encodeStart(this.ops, message).getOrThrow();
    }

    @Nullable
    public Message<? extends MessageBody> decode(@NotNull String string) {
        if (!this.isMessage(string)) {
            return null;
        }
        String body = this.getBody(string);
        if (body == null) {
            return null;
        }
        T element = this.parser.apply(body);
        DataResult<Pair<Message<? extends MessageBody>, T>> result = Versions.MESSAGE_CODEC.decode(this.ops, element);

        if (result.isError()) {
            return null;
        }
        return result.getOrThrow().getFirst();
    }

}
