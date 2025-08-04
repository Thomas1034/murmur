package com.startraveler.murmur.api.example.message;

import com.google.gson.JsonElement;
import com.startraveler.murmur.Constants;
import com.startraveler.murmur.api.MurmurContext;
import com.startraveler.murmur.registry.CodecRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ExampleMessageTypes {

    public static final MurmurContext<JsonElement> MURMUR = MurmurContext.basic(Constants.MOD_ID);

    public static final CodecRegistry.CodecRegistryEntry<ExampleMessageBody> EXAMPLE_MESSAGE_TYPE = MURMUR.registerMessageType(
            Constants.id("example_type"),
            ExampleMessageBody.CODEC,
            (@NotNull ExampleMessageBody body, Object[] payload) -> {
                Object tentativeUUID = payload[0];
                UUID sender = (tentativeUUID instanceof UUID) ? (UUID) tentativeUUID : null;

                boolean isSentByLocal = (sender != null) && Minecraft.getInstance().isLocalPlayer(sender);
                if (!isSentByLocal) {
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal("Received example message: (" + body.x() + ", " + body.y() + ", " + body.z() + ")"));
                }
            }
    );
}
