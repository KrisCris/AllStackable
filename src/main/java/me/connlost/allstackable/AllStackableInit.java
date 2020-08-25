package me.connlost.allstackable;

import me.connlost.allstackable.command.CommandSetMaxCount;
import me.connlost.allstackable.config.ConfigManager;
import me.connlost.allstackable.util.IItemMaxCount;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.io.File;
import java.util.Map;
import java.util.Set;


public class AllStackableInit implements ModInitializer {



	@Override
	public void onInitialize() {
		ConfigManager cm = ConfigManager.getConfigManager();
		for (Map.Entry<String, Integer> entry: cm.loadConfig(new File("config/stack-all.json")).entrySet()){
			((IItemMaxCount)Registry.ITEM.get(new Identifier(entry.getKey()))).setMaxCount(entry.getValue());
		}

		CommandSetMaxCount.register();


	}


}
