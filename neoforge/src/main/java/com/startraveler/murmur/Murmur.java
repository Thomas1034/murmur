package com.startraveler.murmur;


import com.startraveler.murmur.api.MurmurContext;
import com.startraveler.murmur.api.example.message.ExampleMessageBody;
import com.startraveler.murmur.api.example.message.ExampleMessageTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class Murmur {

    public Murmur(IEventBus eventBus) {

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        CommonClass.init();
        NeoForge.EVENT_BUS.addListener(Murmur::handleChatReceivedMessage);
        NeoForge.EVENT_BUS.addListener(Murmur::sendMessageOnUseForTesting);
        // NeoForge.EVENT_BUS.addListener(Murmur::spamForTesting);
    }

    public static void spamForTesting(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide) {
            Constants.LOG.warn("Ticking client side for {}", event.getEntity().getStringUUID());
            if (event.getEntity().getStringUUID().equals(Minecraft.getInstance().player.getStringUUID())) {
                Constants.LOG.warn("Spamming! It is {}", Minecraft.getInstance().player.getStringUUID());
            }
        } else {
            Constants.LOG.warn("Ticking server side for {}", event.getEntity().getStringUUID());
        }
    }

    public static void sendMessageOnUseForTesting(UseItemOnBlockEvent event) {

        if (event.getUsePhase() != UseItemOnBlockEvent.UsePhase.ITEM_BEFORE_BLOCK) {
            return;
        }

        if (!event.getLevel().isClientSide) {
            return;
        }

        if (event.getItemStack().getItem() != Items.DIAMOND) {
            return;
        }

        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            boolean pickedUpByLocalPlayer = localPlayer.getStringUUID().equals(event.getPlayer().getStringUUID());
            if (pickedUpByLocalPlayer) {
                BlockPos usedOn = event.getPos();
                String message = ExampleMessageTypes.MURMUR.makeMessage(
                        new ExampleMessageBody(
                                usedOn.getX(),
                                usedOn.getY(),
                                usedOn.getZ()
                        ), localPlayer.getUUID()
                );
                localPlayer.connection.sendChat(message);
            }
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

    public static void handleJoinedWorld(ClientPlayerNetworkEvent.LoggingIn event) {


    }
}