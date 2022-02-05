package online.connlost.allstackable.util;

import net.minecraft.item.ItemStack;

public interface IDispenserBlockEntity {
    boolean tryInsertAndStackItem(ItemStack itemStack);
}
