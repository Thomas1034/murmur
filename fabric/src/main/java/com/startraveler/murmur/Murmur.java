package com.startraveler.murmur;

import com.mojang.authlib.GameProfile;
import com.startraveler.murmur.api.MurmurContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public class Murmur implements ModInitializer {

    @Override
    public void onInitialize() {

        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        CommonClass.init();
        ClientReceiveMessageEvents.CHAT.register((Component component, @Nullable PlayerChatMessage chatMessage, @Nullable GameProfile profile, ChatType.Bound direction, Instant time) -> {
            if (chatMessage != null) {
                boolean anySucceeded = false;
                String message = chatMessage.decoratedContent().getString();
                for (MurmurContext<?> context : MurmurContext.CONTEXTS) {
                    anySucceeded |= context.receiveMessage(message);
                }
                if (anySucceeded) {
                    // TODO Figure out how to cancel events.
                    // event.setCanceled(true);
                }
            }
        });
    }
}
