package me.connlost.allstackable.server.config;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.connlost.allstackable.util.ItemsHelper;
import me.connlost.allstackable.util.NetworkHelper;
import org.apache.commons.lang3.SerializationUtils;


import static me.connlost.allstackable.AllStackableInit.LOG;

final public class ConfigManager {
    private static ConfigManager cm;
    private File configFile;
    private Gson gson;
    private Map<String,Integer> configMap;
    private ItemsHelper itemsHelper;

    private ConfigManager(){
        configMap = new LinkedHashMap<>();
        gson = new Gson();
        itemsHelper = ItemsHelper.getItemsHelper();
    }

    public static ConfigManager getConfigManager(){
        if (cm == null){
            cm = new ConfigManager();
        }
        return cm;
    }

    public Set<Map.Entry<String, Integer>> getConfigEntry(){
        return configMap.entrySet();
    }

    public byte[] getSerializedConfig(){
        return SerializationUtils.serialize((Serializable) configMap);
    }

    public void passConfigFile(File f){
        this.configFile = f;
    }

    public void setupConfig(){
        loadConfig();
        itemsHelper.setCountByConfig(this.configMap.entrySet(),true);
        NetworkHelper.sentConfigToAll();
        LOG.info("[All Stackable] Config Loaded");
    }

    public Map<String, Integer> loadConfig(){
        if (this.configFile.exists()){
            try (FileReader reader = new FileReader(this.configFile)){
                configMap = gson.fromJson(reader, new TypeToken<LinkedHashMap<String, Integer>>(){}.getType());
            } catch (IOException e) {
                LOG.error("[All Stackable] Failed to parse config");
                throw new RuntimeException("[All Stackable] Could not parse config", e);
            }
        } else {
            this.configMap.clear();
            writeConfig();
        }

        return configMap;
    }

    private void writeConfig(){
        File dir = configFile.getParentFile();

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                LOG.error("[AllStackable] Failed to create the parent directory");
                throw new RuntimeException("[AllStackable] Failed to create the parent directory");
            }
        } else if (!dir.isDirectory()) {
            LOG.error("[AllStackable] Failed to create config file");
            throw new RuntimeException("[AllStackable] The parent is not a directory");
        }

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(configMap, writer);
        } catch (IOException e) {
            LOG.error("[AllStackable] Failed to save config");
            throw new RuntimeException("[AllStackable] Could not save config file", e);
        }
    }

    public Map<String, Integer> syncConfig(){
        configMap = itemsHelper.getNewConfigMap();
        writeConfig();
        NetworkHelper.sentConfigToAll();
        return configMap;
    }

    public void resetAll(){
        configMap.clear();
        writeConfig();
        NetworkHelper.sentConfigToAll();
    }


}
