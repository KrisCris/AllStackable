package online.connlost.allstackable.server;

import online.connlost.allstackable.server.config.ConfigManager;
import online.connlost.allstackable.util.NetworkHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import online.connlost.allstackable.AllStackableInit;


public class Server {
    public static MinecraftServer minecraft_server;
    public static ConfigManager config_manager = ConfigManager.getConfigManager();

    public static void onServerLoaded(MinecraftServer ms){
        minecraft_server = ms;
        config_manager.passConfigFile(minecraft_server.getSavePath(WorldSavePath.ROOT).resolve("allstackable-config.json").toFile());
        config_manager.setupConfig();
        AllStackableInit.LOGGER.info("[All Stackable] Loaded!");
    }

    public static void onPlayerJoin(ServerPlayerEntity player){
        NetworkHelper.sentConfigToPlayer(player);
    }

}
