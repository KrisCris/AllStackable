package online.connlost.allstackable.client;

import online.connlost.allstackable.server.config.ConfigManager;
import online.connlost.allstackable.util.ItemsHelper;
import online.connlost.allstackable.AllStackableInit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigSync {
    private static ItemsHelper itemsHelper = ItemsHelper.getItemsHelper();

    /**
     * Items' stacksize is directly modified, no config is held for that in the "client" side.
     * In contrast, we do store rules in client side as some mixins in client side classes need it.
     * @param configList
     */
    public static void syncConfig(ArrayList<LinkedHashMap<String, Integer>> configList){
        AllStackableInit.LOGGER.info("[All Stackable] [Client] Sync config from server side!");
        itemsHelper.setCountByConfig(configList.get(0).entrySet(), false);
        ConfigManager.getConfigManager().setRulesMap(configList.get(1));
        AllStackableInit.LOGGER.info("[All Stackable] [Client] Sync rules:");
        for (Map.Entry<String, Integer> rule: configList.get(1).entrySet()){
            String tag;
            switch (rule.getValue()){
                case 0: tag = "false";break;
                case 1: tag = "true";break;
                default: tag = rule.getValue().toString();
            }
            AllStackableInit.LOGGER.info("\t["+rule.getKey()+"] = "+tag);
        }
        AllStackableInit.LOGGER.info("[All Stackable] [Client] Sync finished.");
    }

    public static void resetConfig(){
        itemsHelper.resetAll(false);
        ConfigManager.getConfigManager().setRulesMap(ConfigManager.getConfigManager().defaultRules(false));
    }
}
