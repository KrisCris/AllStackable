package me.connlost.allstackable.server;

import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.NetworkHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import static me.connlost.allstackable.AllStackableInit.LOG;


public class Server {
    public static MinecraftServer minecraft_server;
    public static ConfigManager config_manager = ConfigManager.getConfigManager();

    public static void onServerLoaded(MinecraftServer ms){
        minecraft_server = ms;
        System.out.println(minecraft_server.getLevelStorage().getSavesDirectory());
        config_manager.passConfigFile(minecraft_server.getLevelStorage().getSavesDirectory().resolve("allstackable-config.json").toFile());
        config_manager.setupConfig();
        LOG.info("[All Stackable] Loaded!");
    }

    public static void onPlayerJoin(ServerPlayerEntity player){
        NetworkHelper.sentConfigToPlayer(player);
    }

}
