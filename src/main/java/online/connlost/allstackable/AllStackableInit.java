package online.connlost.allstackable;

import online.connlost.allstackable.server.Server;
import online.connlost.allstackable.server.command.StackSizeCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class AllStackableInit implements ModInitializer {
	public static final Identifier SHARE_CONFIG_PACKET_ID = new Identifier("allstackable", "config");
	public static final Logger LOGGER = LogManager.getLogger();
	

	@Override
	public void onInitialize() {
		LOGGER.info("[All Stackable] Start loading!");
		StackSizeCommand.register();
		LOGGER.info("[All Stackable] Command registered.");
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			Server.onServerLoaded(server);
		});

	}


}
