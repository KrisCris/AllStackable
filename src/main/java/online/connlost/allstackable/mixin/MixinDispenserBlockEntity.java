package online.connlost.allstackable.mixin;

import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import online.connlost.allstackable.util.IDispenserBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DispenserBlockEntity.class)
public class MixinDispenserBlockEntity implements IDispenserBlockEntity {
    @Shadow
    private DefaultedList<ItemStack> inventory;


    @Override
    public boolean tryInsertAndStackItem(ItemStack itemStack) {
        boolean inserted = false;
        for(int i = 0; i < this.inventory.size(); ++i) {
            ItemStack invStack = this.inventory.get(i);
            if (invStack.getItem() == itemStack.getItem() && invStack.getCount() < invStack.getMaxCount()) {
                invStack.increment(1);
                inserted = true;
                break;
            }
        }
        return inserted;
    }
}
