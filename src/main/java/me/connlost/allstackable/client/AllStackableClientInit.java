package me.connlost.allstackable.client;

import me.connlost.allstackable.AllStackableInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.SerializationUtils;


public class AllStackableClientInit implements ClientModInitializer {

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(AllStackableInit.SHARE_CONFIG_PACKET_ID, AllStackableClientInit::configHandler);
    }

    private static void configHandler(PacketContext packetContext, PacketByteBuf packetByteBuf) {
        LinkedHashMap<String, Integer> configMap = SerializationUtils.deserialize(packetByteBuf.readByteArray());
        ConfigSync.syncConfig(configMap);

    }
}
