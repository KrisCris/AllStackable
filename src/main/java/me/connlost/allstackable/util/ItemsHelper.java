package me.connlost.allstackable.util;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class ItemsHelper {
    private Map<Integer, LinkedList<Item>> defaultMaxCountMap;
    private static ItemsHelper itemsHelper;

    private ItemsHelper(){
        defaultMaxCountMap = new LinkedHashMap<>();
    }

    public static ItemsHelper getItemsHelper(){
        if (itemsHelper == null){
            itemsHelper = new ItemsHelper();
        }
        return itemsHelper;
    }

    /**
     * Get all Items and put them into different groups of vanilla maxCount.
     */
    private void initDefaultMaxCount(){
        for (Map.Entry<RegistryKey<Item>, Item> itemEntry : getItemSet()){
            Item item = itemEntry.getValue();
            int vanillaMaxCount = ((IItemMaxCount)item).getVanillaMaxCount();
            if (!defaultMaxCountMap.containsKey(vanillaMaxCount)){
                defaultMaxCountMap.put(vanillaMaxCount, new LinkedList<>());
            }
            defaultMaxCountMap.get(vanillaMaxCount).add(item);
        }
    }


    public void resetAll(){
        for (Map.Entry<RegistryKey<Item>, Item> itemEntry : getItemSet()){
            Item item = itemEntry.getValue();
            ((IItemMaxCount)item).revert();
        }
    }

    public void resetItem(Item item){
        setSingle(item, getDefaultCount(item));
    }

    public void setCountByConfig(Set<Map.Entry<String, Integer>> configSet){
        for (Map.Entry<String, Integer> entry: configSet){
            ((IItemMaxCount)Registry.ITEM.get(new Identifier(entry.getKey()))).setMaxCount(entry.getValue());
        }
    }

    public int getDefaultCount(Item item){
        return ((IItemMaxCount)item).getVanillaMaxCount();
    }

    public int getCurrentCount(Item item){
        return item.getMaxCount();
    }

    public void setSingle(Item item, int count){
        ((IItemMaxCount)item).setMaxCount(count);

    }

    public LinkedList<Item> getAllModifiedItem(){
        LinkedList<Item> list = new LinkedList<>();
        for (Map.Entry<RegistryKey<Item>, Item> itemEntry: getItemSet()){
            Item item = itemEntry.getValue();
            if (getDefaultCount(item) != getCurrentCount(item) && !list.contains(item)){
                list.add(item);
            }
        }
        return list;
    }

    public LinkedHashMap<String, Integer> getNewConfigMap(){
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        for (Map.Entry<RegistryKey<Item>, Item> itemEntry: getItemSet()){
            Item item = itemEntry.getValue();
            if (getDefaultCount(item) != getCurrentCount(item) && !map.containsKey(item.toString())){
                map.put(item.toString(), item.getMaxCount());
            }
        }
        return map;
    }


    private Set<Map.Entry<RegistryKey<Item>, Item>> getItemSet(){
        return Registry.ITEM.getEntries();
    }


}