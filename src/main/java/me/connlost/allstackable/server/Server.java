package me.connlost.allstackable.server;

import com.mojang.brigadier.CommandDispatcher;
import me.connlost.allstackable.AllStackableInit;
import me.connlost.allstackable.server.command.SetMaxCommand;
import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.NetworkHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;


public class Server {
    public static MinecraftServer minecraft_server;
    public static ConfigManager config_manager = ConfigManager.getConfigManager();

    public static void onServerLoaded(MinecraftServer ms){
        minecraft_server = ms;
        config_manager.passConfigFile(minecraft_server.getSavePath(WorldSavePath.ROOT).resolve("all_stackable.json").toFile());
        config_manager.setupConfig();
        AllStackableInit.LOG.info("[All Stackable] Loaded!");
    }

    public static void onPlayerJoin(ServerPlayerEntity player){
        NetworkHelper.sentConfigToPlayer(player);
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher){
        SetMaxCommand.register(dispatcher);
    }
}
