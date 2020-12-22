package me.connlost.allstackable.server.config;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.connlost.allstackable.util.ItemsHelper;
import me.connlost.allstackable.util.NetworkHelper;
import org.apache.commons.lang3.SerializationUtils;


import static me.connlost.allstackable.AllStackableInit.LOGGER;

final public class ConfigManager {
    private static ConfigManager cm;
    private File configFile;
    private Gson gson;
    private ItemsHelper itemsHelper;
    private ArrayList<LinkedHashMap<String, Integer>> configList;

    @Deprecated
    public static boolean allowItemShulkerStack = true;

    public static ConfigManager getConfigManager(){
        if (cm == null){
            cm = new ConfigManager();
        }
        return cm;
    }

    private ConfigManager(){
        initConfigList();
        gson = new Gson();
        itemsHelper = ItemsHelper.getItemsHelper();
    }

    private void initConfigList(){
        LinkedHashMap<String,Integer> itemsMap = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> rulesMap = setupRulesMap();
        configList = new ArrayList<>();
        configList.add(itemsMap);
        configList.add(rulesMap);
    }

    public LinkedHashMap<String, Integer> setupRulesMap(){
        LinkedHashMap<String, Integer> m = new LinkedHashMap<>();
        m.put("stackEmptyShulkerBoxOnly", 0);
        return m;
    }

    public int getRuleSetting(String str){
        if (configList.get(1).containsKey(str)){
            return configList.get(1).get(str);
        } else {
            LOGGER.error("No such rule key");
            return -1;
        }
    }

    public void setRulesMap(LinkedHashMap<String, Integer> newRules){
        this.configList.set(1,newRules);
    }

    public byte[] getSerializedConfig(){
        return SerializationUtils.serialize((Serializable) configList);
    }

    public void passConfigFile(File f){
        this.configFile = f;
    }

    public void setupConfig(){
        loadConfig();
        itemsHelper.setCountByConfig(this.configList.get(0).entrySet(),true);
        NetworkHelper.sentConfigToAll();
        LOGGER.info("Config Loaded");
    }

    public ArrayList<LinkedHashMap<String, Integer>> loadConfig(){
        if (this.configFile.exists()){
            try (FileReader reader = new FileReader(this.configFile)){
                configList = gson.fromJson(reader, new TypeToken<ArrayList<LinkedHashMap<String, Integer>>>(){}.getType());
//                itemsMap = gson.fromJson(reader, new TypeToken<LinkedHashMap<String, Integer>>(){}.getType());
            } catch (IOException e) {
                LOGGER.error("Failed to parse config");
                throw new RuntimeException("Could not parse config", e);
            }
        } else {
            initConfigList();
            // for those old users
            String path = this.configFile.getAbsolutePath();
            String oldPath = path.substring(0, path.length()-"allstackable-config.json".length())+"all_stackable.json";
            File oldFile = new File(oldPath);
            if (oldFile.exists()){
                LOGGER.info("Find config file for older version of AllStackable, converting!");
                try (FileReader reader = new FileReader(oldFile)){
                configList.set(0, gson.fromJson(reader, new TypeToken<LinkedHashMap<String, Integer>>(){}.getType()));
                } catch (IOException e) {
                    LOGGER.error("Failed to parse old config");
                    throw new RuntimeException("Could not parse config", e);
                }
            }
            //
            writeConfig();
        }

        return configList;
    }

    private void writeConfig(){
        File dir = configFile.getParentFile();

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                LOGGER.error("Failed to create the parent directory");
                throw new RuntimeException("Failed to create the parent directory");
            }
        } else if (!dir.isDirectory()) {
            LOGGER.error("Failed to create config file");
            throw new RuntimeException("The parent is not a directory");
        }

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(configList, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save config");
            throw new RuntimeException("Could not save config file", e);
        }
    }

    public ArrayList<LinkedHashMap<String, Integer>> syncConfig(){
        configList.set(0, itemsHelper.getNewConfigMap());
        writeConfig();
        NetworkHelper.sentConfigToAll();
        return configList;
    }

    public void resetAllItems(){
        configList.get(0).clear();
        writeConfig();
        NetworkHelper.sentConfigToAll();
    }


}
