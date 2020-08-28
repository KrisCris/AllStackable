package me.connlost.allstackable.util;

import io.netty.buffer.Unpooled;
import me.connlost.allstackable.AllStackableInit;
import me.connlost.allstackable.server.config.ConfigManager;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class NetworkHelper {

    public static void sentConfigToPlayers(List<ServerPlayerEntity> players){
        for (ServerPlayerEntity player:players){
            sentConfigToPlayer(player);
        }
    }

    public static void sentConfigToPlayer(ServerPlayerEntity player){
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeByteArray(ConfigManager.getConfigManager().getSerializedConfig());
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(
                player,
                AllStackableInit.SHARE_CONFIG_PACKET_ID,
                passedData
        );
    }
}
