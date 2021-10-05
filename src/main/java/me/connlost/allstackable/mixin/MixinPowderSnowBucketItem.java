package me.connlost.allstackable.mixin;

import me.connlost.allstackable.util.ItemsHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PowderSnowBucketItem.class)
public class MixinPowderSnowBucketItem {

    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"))
    private void stackableSnowBucket(PlayerEntity instance, Hand hand, ItemStack itemStack, ItemUsageContext context){
        if (ItemsHelper.isModified(context.getStack()) && context.getStack().getCount() > 0) {
            ItemsHelper.insertNewItem(context.getPlayer(), new ItemStack(Items.BUCKET));
        }
    }
}