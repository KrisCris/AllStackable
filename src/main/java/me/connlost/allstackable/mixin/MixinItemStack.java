package me.connlost.allstackable.mixin;

import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.IItemMaxCount;
import me.connlost.allstackable.util.ItemsHelper;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

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


    @Inject(method = "damage(ILjava/util/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"), cancellable = true)
    private void splitDamageable(int amount, Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir){
        ItemStack self = (ItemStack) (Object) this;
        if (self.getCount() > 1){
            ItemStack restStack = self.copy();
            restStack.decrement(1);
            ItemsHelper.insertNewItem(player, restStack);
            self.setCount(1);
        }
    }

//    @Inject(method = "isStackable", at = @At(value = "HEAD"), cancellable = true)
//    private void makeToolsStackable(CallbackInfoReturnable<Boolean> cir){
//        ItemStack self = (ItemStack) (Object) this;
//        if (self.getMaxCount() > 1 && ((IItemMaxCount) self.getItem()).getVanillaMaxCount() != self.getMaxCount()){
//            cir.setReturnValue(true);
//        }
//    }
}
