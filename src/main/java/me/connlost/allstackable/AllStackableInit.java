package me.connlost.allstackable;

import me.connlost.allstackable.server.command.CommandSetMaxCount;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;



public class AllStackableInit implements ModInitializer {
	public static final Identifier SHARE_CONFIG_PACKET_ID = new Identifier("allstackable", "config");



	@Override
	public void onInitialize() {
		CommandSetMaxCount.register();
	}


}
