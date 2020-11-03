package me.connlost.allstackable.mixin;

import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.ItemsHelper;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Deprecated
@Mixin(PlayerInventory.class)
public class MixinPlayerInventory {

    @Inject(method = "canStackAddMore", at=@At("HEAD"), cancellable = true)
    private void stopMergeItemSBox(ItemStack existingStack, ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if (!ConfigManager.allowItemShulkerStack){
            if ((stack.getItem() instanceof BlockItem) && (((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock)) {
                if (ItemsHelper.shulkerBoxHasItems(stack)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
