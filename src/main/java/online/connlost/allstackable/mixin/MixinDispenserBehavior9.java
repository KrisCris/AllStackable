package online.connlost.allstackable.mixin;

import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import online.connlost.allstackable.util.IDispenserBlockEntity;
import online.connlost.allstackable.util.ItemsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/block/dispenser/DispenserBehavior$9")
public class MixinDispenserBehavior9 {

    @Redirect(
            method = "Lnet/minecraft/block/dispenser/ItemDispenserBehavior;dispenseSilently(Lnet/minecraft/util/math/BlockPointer;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;",
            at = @At(
                    target = "Lnet/minecraft/block/entity/DispenserBlockEntity;addToFirstFreeSlot(Lnet/minecraft/item/ItemStack;)I",
                    value = "INVOKE"
            ))
    public int tryStack(DispenserBlockEntity instance, ItemStack stack) {
        int ret = instance.addToFirstFreeSlot(stack);
        if(ItemsHelper.isModified(stack) && ret < 0) {
            ret = ((IDispenserBlockEntity) instance).tryInsertAndStackItem(stack) ? 1 : -1;
        }
        return ret;
    }
}
