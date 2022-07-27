package online.connlost.allstackable.mixin;

import online.connlost.allstackable.util.IDispenserBlockEntity;
import online.connlost.allstackable.util.ItemsHelper;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/block/dispenser/DispenserBehavior$8")
public class MixinDispenserBehavior8 {
    @Shadow
    @Final
    private ItemDispenserBehavior fallbackBehavior;

    /**
     * Remove one bucket at a time instead of deleting the entire stack.
     * If there's no room for the empty bucket in the dispenser, have it be
     * dispensed instead.
     * This prevents entire stacks from being deleted at a time.
     **/
    @Inject(
            method = "dispenseSilently",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/FluidModificationItem;onEmptied(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/math/BlockPos;)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true)
    public void dispenseOne(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (ItemsHelper.isModified(stack) && stack.getCount() > 1) {
            ItemStack newStack = stack.copy();
            newStack.decrement(1);
//            if (((DispenserBlockEntity) pointer.getBlockEntity()).addToFirstFreeSlot(Items.BUCKET.getDefaultStack()) < 0) {
                if (!((IDispenserBlockEntity) pointer.getBlockEntity()).tryInsertAndStackItem(Items.BUCKET.getDefaultStack())) {
                    this.fallbackBehavior.dispense(pointer, Items.BUCKET.getDefaultStack());
                }
//            }
            cir.setReturnValue(newStack);
        }
    }
}
