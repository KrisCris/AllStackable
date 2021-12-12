package online.connlost.allstackable.util;

import io.netty.buffer.Unpooled;
import online.connlost.allstackable.AllStackableInit;
import online.connlost.allstackable.server.Server;
import online.connlost.allstackable.server.config.ConfigManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

import static online.connlost.allstackable.AllStackableInit.LOGGER;

public class NetworkHelper {

    public static void sentConfigToAll(){
        if (Server.minecraft_server != null){
            List<ServerPlayerEntity> players = Server.minecraft_server.getPlayerManager().getPlayerList();
            for (ServerPlayerEntity player:players){
                sentConfigToPlayer(player);
            }
        } else {
            LOGGER.warn("[All Stackable] Server hasn't been loaded.");
        }
    }

    public static void sentConfigToPlayer(ServerPlayerEntity player){
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeByteArray(ConfigManager.getConfigManager().getSerializedConfig());
        ServerPlayNetworking.send(
                player,
                AllStackableInit.SHARE_CONFIG_PACKET_ID,
                passedData
        );
    }
}
