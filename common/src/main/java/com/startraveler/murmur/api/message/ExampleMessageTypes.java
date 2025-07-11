package com.startraveler.murmur.api.message;

import com.google.gson.JsonElement;
import com.startraveler.murmur.Constants;
import com.startraveler.murmur.api.MurmurContext;
import com.startraveler.murmur.registry.CodecRegistry;

public class ExampleMessageTypes {

    public static final MurmurContext<JsonElement> MURMUR = MurmurContext.basic(Constants.MOD_ID);

    public static final CodecRegistry.CodecRegistryEntry<ExampleMessageBody> EXAMPLE_MESSAGE_TYPE = MURMUR.registerMessageType(
            Constants.id("example_type"),
            ExampleMessageBody.CODEC,
            (ExampleMessageBody body) -> {Constants.LOG.warn("Received example message: {}, {}", body.a(), body.b());}
    );
}
