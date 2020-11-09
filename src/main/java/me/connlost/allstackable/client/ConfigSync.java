package me.connlost.allstackable.client;

import me.connlost.allstackable.util.ItemsHelper;

import static me.connlost.allstackable.AllStackableInit.LOG;

import java.util.Map;

public class ConfigSync {
    private static ItemsHelper itemsHelper = ItemsHelper.getItemsHelper();

    public static void syncConfig(Map<String, Integer> configMap){
        LOG.info("[All Stackable][Client] Sync config from server side!");
        itemsHelper.setCountByConfig(configMap.entrySet(), false);
        LOG.info("[All Stackable][Client] Sync finished.");
    }

    public static void resetConfig(){
        itemsHelper.resetAll(false);
//        LOG.info("[All Stackable][Client] Config reset!");
    }
}
