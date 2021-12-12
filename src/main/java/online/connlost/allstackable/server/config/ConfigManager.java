package online.connlost.allstackable.server.config;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import online.connlost.allstackable.util.ItemsHelper;
import online.connlost.allstackable.util.NetworkHelper;
import net.fabricmc.loader.api.FabricLoader;
import online.connlost.allstackable.AllStackableInit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;

final public class ConfigManager {
    private static ConfigManager cm;
    private File configFile;
    private File globalConfigFile;
    private Gson gson;
    private ItemsHelper itemsHelper;
    private ArrayList<LinkedHashMap<String, Integer>> configList;
    private ArrayList<LinkedHashMap<String, Integer>> globalConfigList;

    @Deprecated
    public static boolean allowItemShulkerStack = true;

    public static ConfigManager getConfigManager() {
        if (cm == null) {
            cm = new ConfigManager();
        }
        return cm;
    }

    private ConfigManager() {
        initConfigList();
        gson = new Gson();
        globalConfigFile = FabricLoader.getInstance().getConfigDir().resolve("allstackable-global-config.json").toFile();
        itemsHelper = ItemsHelper.getItemsHelper();
    }

    public LinkedHashMap<String, Integer> defaultRules(boolean global) {
        LinkedHashMap<String, Integer> defaultRules = new LinkedHashMap<>();
        if (global) {
            defaultRules.put("applyGlobalConfigToAllNewGames", 0);
        }
        defaultRules.put("permissionLevel", 4);
        defaultRules.put("stackEmptyShulkerBoxOnly", 0);
        return defaultRules;
    }

    private void initConfigList() {
        LinkedHashMap<String, Integer> itemsMap = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> rulesMap = defaultRules(false);
        configList = new ArrayList<>();
        configList.add(itemsMap);
        configList.add(rulesMap);
    }

    public int getRuleSetting(String str) {
        if (configList.get(1).containsKey(str)) {
            return configList.get(1).get(str);
        } else {
            AllStackableInit.LOGGER.error("[All Stackable] No such rule key");
            return -1;
        }
    }

    // used by client side
    public void setRulesMap(LinkedHashMap<String, Integer> newRules) {
        if(this.configList == null){
            this.configList = new ArrayList<>();
            this.configList.add(new LinkedHashMap<>());
            this.configList.add(new LinkedHashMap<>());
        }
        this.configList.set(1, newRules);
    }

//    // used by client side
//    public LinkedHashMap<String, Integer> setupRulesMap() {
//        LinkedHashMap<String, Integer> m = new LinkedHashMap<>();
//        m.put("stackEmptyShulkerBoxOnly", 0);
//        return m;
//    }

    public byte[] getSerializedConfig() {
        return SerializationUtils.serialize((Serializable) configList);
    }

    public void passConfigFile(File f) {
        this.configFile = f;
    }

    public void setupConfig() {
        loadConfig();
        itemsHelper.setCountByConfig(this.configList.get(0).entrySet(), true);
        NetworkHelper.sentConfigToAll();
        AllStackableInit.LOGGER.info("[All Stackable] Config Loaded");
    }

    public boolean restoreBackup() {
        File bk = configFile.getParentFile().toPath().resolve("allstackable-config.json.bk").toFile();
        if (bk.exists()) {
            try (FileReader reader = new FileReader(bk)) {
                ArrayList<LinkedHashMap<String, Integer>> tmp = gson.fromJson(reader, new TypeToken<ArrayList<LinkedHashMap<String, Integer>>>() {
                }.getType());
                if (tmp == null || tmp.size() != 2) {
                    bk.delete();
                    AllStackableInit.LOGGER.error("[All Stackable] Corrupted backup detected, removed.");
                    return false;
                }
                configList = tmp;
            } catch (IOException e) {
                AllStackableInit.LOGGER.error("[All Stackable] Failed to parse backup file");
                throw new RuntimeException("Could not parse backup file", e);
            }
            this.writeConfig(this.configFile, this.configList);
            this.setupConfig();
            AllStackableInit.LOGGER.info("[All Stackable] Backup config restored!");
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<LinkedHashMap<String, Integer>> loadConfig() {
        this.tryReadGlobalConfig();
        if (this.configFile.exists()) {
            try (FileReader reader = new FileReader(this.configFile)) {
                configList = gson.fromJson(reader, new TypeToken<ArrayList<LinkedHashMap<String, Integer>>>() {
                }.getType());
                if (configList == null || configList.size() != 2) {
                    this.configFile.delete();
                    AllStackableInit.LOGGER.error("[All Stackable] Corrupted config detected, reset.");
                    return loadConfig();
                }
                configList.set(1, makeRulesUpdated(configList.get(1), false));
            } catch (IOException e) {
                AllStackableInit.LOGGER.error("[All Stackable] Failed to parse config");
                throw new RuntimeException("Could not parse config", e);
            }
        } else {
            // for those who may still using old config
            String path = this.configFile.getAbsolutePath();
            String oldPath = path.substring(0, path.length() - "allstackable-config.json".length()) + "all_stackable.json";
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {
                AllStackableInit.LOGGER.info("[All Stackable] Find config file for older version of AllStackable, converting!");
                this.initConfigList();
                try (FileReader reader = new FileReader(oldFile)) {
                    LinkedHashMap<String, Integer> tmp = gson.fromJson(reader, new TypeToken<LinkedHashMap<String, Integer>>() {
                    }.getType());
                    if (tmp == null) {
                        oldFile.delete();
                        AllStackableInit.LOGGER.error("[All Stackable] Corrupted old config detected, removed.");
                        return loadConfig();
                    }
                    configList.set(0, tmp);
                    oldFile.delete();
                } catch (IOException e) {
                    AllStackableInit.LOGGER.error("[All Stackable] Failed to parse old config");
                    throw new RuntimeException("Could not parse config", e);
                }
            } else {
                // try apply global settings
                if (!tryApplyGlobalToLocalConfig(false)) {
                    this.initConfigList();
                }
            }

            writeConfig(this.configFile, this.configList);
        }

        return configList;
    }

    private void writeConfig(File configFile, ArrayList<LinkedHashMap<String, Integer>> configData) {
        File dir = configFile.getParentFile();

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                AllStackableInit.LOGGER.error("[All Stackable] Failed to create the parent directory");
                throw new RuntimeException("Failed to create the parent directory");
            }
        } else if (!dir.isDirectory()) {
            AllStackableInit.LOGGER.error("[All Stackable] Failed to create config file");
            throw new RuntimeException("The parent is not a directory");
        }

        // bk
        if (configFile.exists()){
            File bk = dir.toPath().resolve(configFile.getName() + ".bk").toFile();
            try {
                FileUtils.copyFile(configFile, bk);
            } catch (IOException e) {
                AllStackableInit.LOGGER.error("[All Stackable] Failed to backup existing config");
                throw new RuntimeException("Failed to backup existing config", e);
            }
        }

        // write
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(configData, writer);
        } catch (IOException e) {
            AllStackableInit.LOGGER.error("[All Stackable] Failed to save config");
            throw new RuntimeException("Could not save config file", e);
        }
    }

    /**
     * Sync the applied config with both file and peer clients.
     * @return latest config list
     */
    public ArrayList<LinkedHashMap<String, Integer>> syncConfig() {
        configList.set(0, itemsHelper.getNewConfigMap());
        writeConfig(this.configFile, this.configList);
        NetworkHelper.sentConfigToAll();
        return configList;
    }

    public void resetAllItems() {
        configList.get(0).clear();
        writeConfig(this.configFile, this.configList);
        NetworkHelper.sentConfigToAll();
    }

    public void updateGlobalConfig(boolean updateStackableList, boolean allowAutoApply) {
        if (updateStackableList) {
            this.globalConfigList.get(0).clear();
            for (Map.Entry<String, Integer> entry : this.configList.get(0).entrySet()) {
                this.globalConfigList.get(0).put(entry.getKey(), entry.getValue());
            }
            this.globalConfigList.set(1, makeRulesUpdated(this.configList.get(1), true));
        }
        if (allowAutoApply) {
            this.globalConfigList.get(1).put("applyGlobalConfigToAllNewGames", 1);
        }

        this.writeConfig(this.globalConfigFile, this.globalConfigList);
        AllStackableInit.LOGGER.info("[All Stackable] Global Config Updated");
    }

    private void tryReadGlobalConfig() {
        if (this.globalConfigFile.exists()) {
            try (FileReader reader = new FileReader(this.globalConfigFile)) {
                this.globalConfigList = gson.fromJson(reader, new TypeToken<ArrayList<LinkedHashMap<String, Integer>>>() {
                }.getType());

                if (globalConfigList == null || globalConfigList.size() != 2) {
                    this.globalConfigFile.delete();
                    AllStackableInit.LOGGER.error("[All Stackable] Corrupted global config detected, reset.");
                    tryReadGlobalConfig();
                    return;
                }

                this.globalConfigList.set(1, makeRulesUpdated(this.globalConfigList.get(1), true));
                this.writeConfig(this.globalConfigFile, this.globalConfigList);

            } catch (IOException e) {
                AllStackableInit.LOGGER.error("[All Stackable] Failed to parse a global config");
                throw new RuntimeException("Could not parse global config", e);
            }
        } else {
            this.globalConfigList = new ArrayList<>();
            this.globalConfigList.add(new LinkedHashMap<String, Integer>());
            this.globalConfigList.add(this.defaultRules(true));
            this.writeConfig(this.globalConfigFile, this.globalConfigList);
            AllStackableInit.LOGGER.info("[All Stackable] New global config created, disabled by default.");
        }

    }

    private boolean tryApplyGlobalToLocalConfig(boolean forced) {
        if (this.globalConfigList.get(1).get("applyGlobalConfigToAllNewGames") == 1 || forced) {
            LinkedHashMap<String, Integer> itemsMap = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : this.globalConfigList.get(0).entrySet()) {
                itemsMap.put(entry.getKey(), entry.getValue());
            }
            LinkedHashMap<String, Integer> rulesMap = makeRulesUpdated(this.globalConfigList.get(1), false);
            configList = new ArrayList<>();
            configList.add(itemsMap);
            configList.add(rulesMap);
            return true;
        } else {
            return false;
        }
    }

    private LinkedHashMap<String, Integer> makeRulesUpdated(LinkedHashMap<String, Integer> currentMap, boolean global) {
        LinkedHashMap<String, Integer> rulesMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : this.defaultRules(global).entrySet()) {
            if (currentMap.containsKey(entry.getKey())) {
                rulesMap.put(entry.getKey(), currentMap.get(entry.getKey()));
            } else {
                if (global) {
                    if (entry.getKey().equals("applyGlobalConfigToAllNewGames")) {
                        Integer value = this.globalConfigList.get(1).get("applyGlobalConfigToAllNewGames");
                        if (value instanceof Integer){
                            rulesMap.put(entry.getKey(), value);
                            continue;
                        }
                    }
                }
                rulesMap.put(entry.getKey(), entry.getValue());
                AllStackableInit.LOGGER.info("[All Stackable] New rule '" + entry.getKey() + "' added to the config file.");
            }
        }
        return rulesMap;
    }

    public void applyGlobalToLocal() {
        this.tryApplyGlobalToLocalConfig(true);
        this.writeConfig(this.configFile, this.configList);
        itemsHelper.setCountByConfig(this.configList.get(0).entrySet(), true);
        NetworkHelper.sentConfigToAll();
        AllStackableInit.LOGGER.info("[All Stackable] Global config loaded");
    }


}
