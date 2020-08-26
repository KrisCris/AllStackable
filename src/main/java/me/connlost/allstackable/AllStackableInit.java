package me.connlost.allstackable;

import me.connlost.allstackable.server.command.CommandSetMaxCount;
import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.IItemMaxCount;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.util.Map;


public class AllStackableInit implements ModInitializer {
	public static final Identifier SHARE_CONFIG_PACKET_ID = new Identifier("allstackable", "config");



	@Override
	public void onInitialize() {
		CommandSetMaxCount.register();
	}


}
