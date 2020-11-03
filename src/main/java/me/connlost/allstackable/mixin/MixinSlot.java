package me.connlost.allstackable.mixin;

import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.ItemsHelper;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Deprecated
@Mixin(Slot.class)
public class MixinSlot {

    @Inject(method = "getMaxItemCount()I", at=@At("HEAD"), cancellable = true)
    private void stopMergeItemSBox(CallbackInfoReturnable<Integer> cir){
        if (!ConfigManager.allowItemShulkerStack){
            Slot self = (Slot)(Object)this;
            ItemStack stack = self.getStack();
            if ((stack.getItem() instanceof BlockItem) && (((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock)) {
                if (ItemsHelper.shulkerBoxHasItems(stack)) {
                    cir.setReturnValue(1);
                }
            }
        }
    }
}
