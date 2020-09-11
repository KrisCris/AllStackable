package me.connlost.allstackable.client;

import me.connlost.allstackable.AllStackableInit;
import me.connlost.allstackable.util.IItemMaxCount;
import me.connlost.allstackable.util.ItemsHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;

public class ConfigSync {
    private static ItemsHelper itemsHelper = ItemsHelper.getItemsHelper();

    public static void syncConfig(Map<String, Integer> configMap){
        AllStackableInit.LOG.info("[All Stackable][Client] Sync config from server side!");
        itemsHelper.setCountByConfig(configMap.entrySet(), false);
        AllStackableInit.LOG.info("[All Stackable][Client] Sync finished.");
    }

    public static void resetConfig(){
        itemsHelper.resetAll(false);
        AllStackableInit.LOG.info("[All Stackable][Client] Config reset!");
    }
}
