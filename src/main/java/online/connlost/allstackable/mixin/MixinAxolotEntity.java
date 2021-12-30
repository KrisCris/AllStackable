package online.connlost.allstackable.mixin;

import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import online.connlost.allstackable.util.ItemsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AxolotlEntity.class)
public class MixinAxolotEntity {
    @Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"), cancellable = true)
    protected void reduceBucketsByOne(PlayerEntity player, Hand hand, ItemStack stack, CallbackInfo ci){
        if (ItemsHelper.isModified(stack) && stack.getCount() > 1){
            if (player.getAbilities().creativeMode) {
                // this is not the vanilla behavior,
                // but I just has no idea how to decrease the stack count by 1 in creative mode -
                // it simply won't change for some reason that I haven't looked into
                // so, I decide to keep your inventory unchanged, lol.
                ;
            }
            else {
                ItemsHelper.insertNewItem(player, new ItemStack(Items.WATER_BUCKET));
                stack.decrement(1);
            }
            ci.cancel();
        }
    }

}
