package com.startraveler.murmur;


import com.startraveler.murmur.api.MurmurContext;
import com.startraveler.murmur.api.message.ExampleMessageBody;
import com.startraveler.murmur.api.message.ExampleMessageTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class Murmur {

    public Murmur(IEventBus eventBus) {

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        CommonClass.init();
        NeoForge.EVENT_BUS.addListener(Murmur::handleChatReceivedMessage);
        NeoForge.EVENT_BUS.addListener(Murmur::sendMessageOnItemPickupForTesting);
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

    // TODO DELETE AFTER TESTING
    public static void sendMessageOnItemPickupForTesting(ItemEntityPickupEvent.Post event) {
        LocalPlayer minecraftPlayer = Minecraft.getInstance().player;
        if (minecraftPlayer != null && minecraftPlayer.getStringUUID().equals(event.getPlayer().getStringUUID())) {
            String message = ExampleMessageTypes.MURMUR.makeMessage(new ExampleMessageBody(
                    minecraftPlayer.getBlockX(),
                    minecraftPlayer.getBlockZ()
            ));
            minecraftPlayer.connection.sendChat(message);
        }
    }
}