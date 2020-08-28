package me.connlost.allstackable.server.config;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.connlost.allstackable.server.Server;
import me.connlost.allstackable.util.ItemsHelper;
import me.connlost.allstackable.util.NetworkHelper;
import org.apache.commons.lang3.SerializationUtils;

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
        itemsHelper.setCountByConfig(this.configMap.entrySet());
    }

    public Map<String, Integer> loadConfig(){
        if (this.configFile.exists()){
            try (FileReader reader = new FileReader(this.configFile)){
                configMap = gson.fromJson(reader, new TypeToken<LinkedHashMap<String, Integer>>(){}.getType());
            } catch (IOException e) {
                throw new RuntimeException("Could not parse config", e);
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
                throw new RuntimeException("Failed to create the parent directory");
            }
        } else if (!dir.isDirectory()) {
            throw new RuntimeException("The parent is not a directory");
        }

        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(configMap, writer);
        } catch (IOException e) {
            throw new RuntimeException("Could not save config file", e);
        }
    }

    public Map<String, Integer> addConfig(String id, int count){
        if (!configMap.containsKey(id)){
            configMap.put(id, count);
        } else {
            configMap.replace(id, count);
        }

        writeConfig();
        //Temp
        NetworkHelper.sentConfigToPlayers(Server.minecraft_server.getPlayerManager().getPlayerList());
        //
        return configMap;
    }

    public void removeConfig(String id){
        if(configMap.containsKey(id)){
            configMap.remove(id);
        }
        writeConfig();
        //Temp
        NetworkHelper.sentConfigToPlayers(Server.minecraft_server.getPlayerManager().getPlayerList());
        //
    }

    public void removeAllConfig(){
        configMap.clear();
        writeConfig();
        //Temp
        NetworkHelper.sentConfigToPlayers(Server.minecraft_server.getPlayerManager().getPlayerList());
        //
    }


}
