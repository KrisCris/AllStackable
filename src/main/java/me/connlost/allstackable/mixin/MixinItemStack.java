package me.connlost.allstackable.mixin;

import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.ItemsHelper;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(method = "getMaxCount", at = @At("HEAD"), cancellable = true)
    private void preventItemSBoxStack(CallbackInfoReturnable<Integer> cir) {
        if (ConfigManager.getConfigManager().getRuleSetting("stackEmptyShulkerBoxOnly") == 1) {
            ItemStack self = (ItemStack) (Object) this;
            if ((self.getItem() instanceof BlockItem) && (((BlockItem) self.getItem()).getBlock() instanceof ShulkerBoxBlock)) {
                if (ItemsHelper.shulkerBoxHasItems(self)) {
                    cir.setReturnValue(1);
                }
            }
        }

    }
}
