package online.connlost.allstackable.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import online.connlost.allstackable.AllStackableInit;
import org.apache.commons.lang3.SerializationUtils;


public class AllStackableClientInit implements ClientModInitializer {

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        ClientPlayConnectionEvents.INIT.register((handler, client) ->{
            ClientPlayNetworking.registerReceiver(
                    AllStackableInit.SHARE_CONFIG_PACKET_ID,
                    (client1, handler1, buf, sender1) -> configHandler(handler1, sender1, client1, buf)
            );
        });
    }

    private void configHandler(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client, PacketByteBuf buf){
        ArrayList<LinkedHashMap<String, Integer>> configList = SerializationUtils.deserialize(buf.readByteArray());
        ConfigSync.syncConfig(configList);
    }
}
