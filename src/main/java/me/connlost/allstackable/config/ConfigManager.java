package me.connlost.allstackable.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

final public class ConfigManager {
    private static ConfigManager cm;
    private File file;
    private Gson gson;
    private Map<String,Integer> map;

    private ConfigManager(){
        map = new LinkedHashMap<>();
        gson = new Gson();
    }

    public static ConfigManager getConfigManager(){
        if (cm == null){
            cm = new ConfigManager();
        }
        return cm;
    }

    public Set<Map.Entry<String, Integer>> getEntry(){
        return map.entrySet();
    }

    public Map<String, Integer> loadConfig(File file){
        this.file = file;
        if (this.file.exists()){
            try (FileReader reader = new FileReader(this.file)){
                map = gson.fromJson(reader, new TypeToken<LinkedHashMap<String, Integer>>(){}.getType());
            } catch (IOException e) {
                throw new RuntimeException("Could not parse config", e);
            }
        } else {
            writeConfig();
        }

        return map;
    }

    private void writeConfig(){
        File dir = file.getParentFile();

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Failed to create the parent directory");
            }
        } else if (!dir.isDirectory()) {
            throw new RuntimeException("The parent is not a directory");
        }

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(map, writer);
        } catch (IOException e) {
            throw new RuntimeException("Could not save config file", e);
        }
    }

    public Map<String, Integer> addConfig(String id, int count){
        if (!map.containsKey(id)){
            map.put(id, count);
        } else {
            map.replace(id, count);
        }

        writeConfig();
        return map;
    }

    public void removeConfig(String id){
        if(map.containsKey(id)){
            map.remove(id);
        }
        writeConfig();
    }

    public void removeAllConfig(){
        map.clear();
        writeConfig();
    }


}
