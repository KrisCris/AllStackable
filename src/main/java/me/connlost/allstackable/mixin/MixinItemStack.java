package me.connlost.allstackable.mixin;

import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.IItemMaxCount;
import me.connlost.allstackable.util.ItemsHelper;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @Redirect(method = "damage(ILjava/util/Random;Lnet/minecraft/server/network/ServerPlayerEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setDamage(I)V"))
    private void splitStackedTools(ItemStack stack, int damage, int amount, Random random, ServerPlayerEntity player){
        Item i = stack.getItem();
        ItemStack rest = null;
        if (stack.getCount()>1 && ((IItemMaxCount)i).getVanillaMaxCount()!=i.getMaxCount()){
            rest = stack.copy();
            rest.decrement(1);
            stack.setCount(1);
        }
        stack.setDamage(damage);
        if (rest != null){
            ItemsHelper.insertNewItem(player,rest);
        }
    }
}
