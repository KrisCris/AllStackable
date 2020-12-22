package me.connlost.allstackable.server;

import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.NetworkHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;


import java.io.File;

import static me.connlost.allstackable.AllStackableInit.LOGGER;


public class Server {
    public static MinecraftServer minecraft_server;
    public static ConfigManager config_manager = ConfigManager.getConfigManager();

    public static void onServerLoaded(MinecraftServer ms){
        minecraft_server = ms;
        File file = minecraft_server.getLevelStorage().resolveFile(minecraft_server.getLevelName(),"allstackable-config.json");
        config_manager.passConfigFile(file);
        config_manager.setupConfig();
        LOGGER.info("Loaded!");
    }

    public static void onPlayerJoin(ServerPlayerEntity player){
        NetworkHelper.sentConfigToPlayer(player);
    }

}
