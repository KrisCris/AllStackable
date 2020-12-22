package me.connlost.allstackable.util;

import io.netty.buffer.Unpooled;
import me.connlost.allstackable.AllStackableInit;
import me.connlost.allstackable.server.Server;
import me.connlost.allstackable.server.config.ConfigManager;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;
import java.util.List;

import static me.connlost.allstackable.AllStackableInit.LOGGER;

public class NetworkHelper {

    public static void sentConfigToAll(){
        if (Server.minecraft_server != null){
            List<ServerPlayerEntity> players = Server.minecraft_server.getPlayerManager().getPlayerList();
            for (ServerPlayerEntity player:players){
                sentConfigToPlayer(player);
            }
        } else {
            LOGGER.warn("Server hasn't been loaded.");
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
