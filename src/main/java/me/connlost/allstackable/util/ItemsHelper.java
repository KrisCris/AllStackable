package me.connlost.allstackable.util;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import static me.connlost.allstackable.AllStackableInit.LOG;

public class ItemsHelper {
    private static ItemsHelper itemsHelper;

    private ItemsHelper() {
    }

    public static ItemsHelper getItemsHelper() {
        if (itemsHelper == null) {
            itemsHelper = new ItemsHelper();
        }
        return itemsHelper;
    }

    public void resetAll(boolean serverSide) {
        for (Identifier id : getItemSet()) {
            Item item = Registry.ITEM.get(id);
            ((IItemMaxCount) item).revert();
        }
//        if (serverSide) LOG.info("[All Stackable] All Reset!");
//        else LOG.info("[All Stackable] [Client] Reset");
    }

    public void resetItem(Item item) {
        setSingle(item, getDefaultCount(item));
//        LOG.info("[All Stackable] Reset " + item.toString());
    }

    public void setCountByConfig(Set<Map.Entry<String, Integer>> configSet, boolean serverSide) {
        resetAll(serverSide);
        for (Map.Entry<String, Integer> entry : configSet) {
            Item item = Registry.ITEM.get(new Identifier(entry.getKey()));

            if (serverSide)
                LOG.info("[All Stackable] Set " + entry.getKey() + " to " + entry.getValue());
            else
                LOG.info("[All Stackable] [Client] Set " + entry.getKey() + " to " + entry.getValue());
            ((IItemMaxCount) item).setMaxCount(entry.getValue());

        }
    }

    public int getDefaultCount(Item item) {
        return ((IItemMaxCount) item).getVanillaMaxCount();
    }

    public int getCurrentCount(Item item) {
        return item.getMaxCount();
    }

    public void setSingle(Item item, int count) {
        ((IItemMaxCount) item).setMaxCount(count);
//        LOG.info("[All Stackable] Set " + item.toString() + " to " + count);
    }

    public LinkedList<Item> getAllModifiedItem() {
        LinkedList<Item> list = new LinkedList<>();
        for (Identifier id : getItemSet()) {
            Item item = Registry.ITEM.get(id);
            if (getDefaultCount(item) != getCurrentCount(item) && !list.contains(item)) {
                list.add(item);
            }
        }
        return list;
    }

    public LinkedHashMap<String, Integer> getNewConfigMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        for (Identifier id : getItemSet()) {
            Item item = Registry.ITEM.get(id);
            String sid = id.toString();
            if (getDefaultCount(item) != getCurrentCount(item) && !map.containsKey(sid)) {
                map.put(sid, item.getMaxCount());
            }
        }
        return map;
    }


    private Set<Identifier> getItemSet() {
        return Registry.ITEM.getIds();
    }


    // From nbt/Tag.java createTag()
    public static final int TAG_END         = 0;
    public static final int TAG_BYTE        = 1;
    public static final int TAG_SHORT       = 2;
    public static final int TAG_INT         = 3;
    public static final int TAG_LONG        = 4;
    public static final int TAG_FLOAT       = 5;
    public static final int TAG_DOUBLE      = 6;
    public static final int TAG_BYTEARRAY   = 7;
    public static final int TAG_STRING      = 8;
    public static final int TAG_LIST        = 9;
    public static final int TAG_COMPOUND    = 10;
    public static final int TAG_INTARRAY    = 11;
    public static final int TAG_LONGARRAY   = 12;

    public static boolean shulkerBoxHasItems(ItemStack stack)
    {
        CompoundTag tag = stack.getTag();

        if (tag == null || !tag.contains("BlockEntityTag", TAG_COMPOUND))
            return false;

        CompoundTag bet = tag.getCompound("BlockEntityTag");
        return bet.contains("Items", TAG_LIST) && !bet.getList("Items", TAG_COMPOUND).isEmpty();
    }

    public static void insertNewItem(PlayerEntity player, Hand hand, ItemStack stack1, ItemStack stack2) {
        if (stack1.isEmpty()){
            player.setStackInHand(hand, stack2);
        } else if (!player.inventory.insertStack(stack2)) {
            player.dropItem(stack2, false);
        } else if (player instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) player).openContainer((Container) player.playerContainer);
        }
    }

    public static void insertNewItem(PlayerEntity player, ItemStack stack2) {
        if (!player.inventory.insertStack(stack2)) {
            player.dropItem(stack2, false);
        } else if (player instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) player).openContainer((Container) player.playerContainer);
        }
    }

    public static boolean isModified(ItemStack s){
        if (s.isEmpty()){
            return false;
        }
        Item i = s.getItem();
        return (((IItemMaxCount)i).getVanillaMaxCount()!=i.getMaxCount())&&s.getCount()>1;
    }
}
