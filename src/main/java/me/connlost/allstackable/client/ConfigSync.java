package me.connlost.allstackable.client;

import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.ItemsHelper;

import static me.connlost.allstackable.AllStackableInit.LOG;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigSync {
    private static ItemsHelper itemsHelper = ItemsHelper.getItemsHelper();

    public static void syncConfig(ArrayList<LinkedHashMap<String, Integer>> configList){
        LOG.info("[All Stackable][Client] Sync config from server side!");
        itemsHelper.setCountByConfig(configList.get(0).entrySet(), false);
        ConfigManager.getConfigManager().setRulesMap(configList.get(1));
        LOG.info("[All Stackable][Client] Sync rules (Default = false) :");
        for (Map.Entry<String, Integer> rules: configList.get(1).entrySet()){
            LOG.info("\t["+rules.getKey()+"] = "+((rules.getValue()==0)?"false":"true"));
        }
        LOG.info("[All Stackable][Client] Sync finished.");
    }

    public static void resetConfig(){
        itemsHelper.resetAll(false);
        ConfigManager.getConfigManager().setRulesMap(ConfigManager.getConfigManager().setupRulesMap());
    }
}
