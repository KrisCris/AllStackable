package me.connlost.allstackable;

import me.connlost.allstackable.server.Server;
import me.connlost.allstackable.server.command.StackSizeCommand;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;


public class AllStackableInit implements ModInitializer {
	public static final Identifier SHARE_CONFIG_PACKET_ID = new Identifier("allstackable", "config");
	public static final Logger LOG = LogManager.getLogger();
	

	@Override
	public void onInitialize() {
		LOG.info("[All Stackable] Start loading!");
		StackSizeCommand.register();
		LOG.info("[All Stackable] Command registered.");
		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
			Server.onServerLoaded(minecraftServer);
		});
	}


}
