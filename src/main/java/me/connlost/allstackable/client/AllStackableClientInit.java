package me.connlost.allstackable.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.SerializationUtils;

import static me.connlost.allstackable.AllStackableInit.SHARE_CONFIG_PACKET_ID;


public class AllStackableClientInit implements ClientModInitializer {

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(SHARE_CONFIG_PACKET_ID, AllStackableClientInit::configHandler);
    }

    private static void configHandler(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        LinkedHashMap<String, Integer> configMap = SerializationUtils.deserialize(packetByteBuf.readByteArray());
        ConfigSync.syncConfig(configMap);

    }
}
