package com.startraveler.murmur.protocol;

import com.startraveler.murmur.protocol.message.Message;
import com.startraveler.murmur.protocol.message.MessageBody;
import com.startraveler.murmur.registry.CodecRegistry;

public class ProtocolCodecRegistries {

    public static final CodecRegistry<MessageBody> MESSAGE_TYPES = new CodecRegistry<>();
    public static final CodecRegistry<Message<? extends MessageBody>> VERSIONS = new CodecRegistry<>();

    private ProtocolCodecRegistries() {
    }
}
