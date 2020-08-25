package me.connlost.allstackable.util;

import net.minecraft.item.Item;
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
        Set<Map.Entry<RegistryKey<Item>, Item>> itemSet =  Registry.ITEM.getEntries();
        for (Map.Entry<RegistryKey<Item>, Item> itemEntry : itemSet){
            Item item = itemEntry.getValue();
            int vanillaMaxCount = ((IItemMaxCount)item).getVanillaMaxCount();
            if (!defaultMaxCountMap.containsKey(vanillaMaxCount)){
                defaultMaxCountMap.put(vanillaMaxCount, new LinkedList<>());
            }
            defaultMaxCountMap.get(vanillaMaxCount).add(item);
        }
    }

    //TODO Lots of works...




}
