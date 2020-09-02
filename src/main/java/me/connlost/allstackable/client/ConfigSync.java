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
        for (Map.Entry<String, Integer> configEntry : configMap.entrySet()){
            ((IItemMaxCount) Registry.ITEM.get(new Identifier(configEntry.getKey()))).setMaxCount(configEntry.getValue());
            AllStackableInit.LOG.info("[All Stackable][Client] Set "+configEntry.getKey()+" to "+configEntry.getValue());
        }
    }

    public static void resetConfig(){
        itemsHelper.resetAll();
        AllStackableInit.LOG.info("[All Stackable][Client] Config reset!");
    }
}
