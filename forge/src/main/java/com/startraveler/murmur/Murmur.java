package com.startraveler.murmur;

import com.startraveler.murmur.api.MurmurContext;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(value = Constants.MOD_ID)
public class Murmur {

    public Murmur() {

        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        // Use Forge to bootstrap the Common mod.
        CommonClass.init();
        MinecraftForge.EVENT_BUS.addListener(Murmur::handleChatReceivedMessage);
        // NeoForge.EVENT_BUS.addListener(Murmur::spamForTesting);
    }

    public static void spamForTesting(TickEvent.PlayerTickEvent.Post event) {
        if (event.player != null && Minecraft.getInstance().player != null && event.player.level().isClientSide) {
            Constants.LOG.warn("Ticking client side for {}", event.player.getStringUUID());
            if (event.player.getStringUUID().equals(Minecraft.getInstance().player.getStringUUID())) {
                Constants.LOG.warn("Spamming! It is {}", Minecraft.getInstance().player.getStringUUID());
            }
        } else {
            Constants.LOG.warn(
                    "Ticking server side for {}",
                    event.player != null ? event.player.getStringUUID() : null
            );
        }
    }

    public static void handleChatReceivedMessage(ClientChatReceivedEvent.Player event) {
        boolean anySucceeded = false;
        String message = event.getPlayerChatMessage().decoratedContent().getString();
        for (MurmurContext<?> context : MurmurContext.CONTEXTS) {
            anySucceeded |= context.receiveMessage(message);
        }
        if (anySucceeded) {
            event.setCanceled(true);
        }
    }
}