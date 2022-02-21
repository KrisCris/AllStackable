package online.connlost.allstackable.mixin;

import online.connlost.allstackable.util.ItemsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StewItem.class)
public class MixinMushroomStewItem {

    @Inject(method = "finishUsing", at = @At(value = "NEW", target = "net/minecraft/item/ItemStack"), cancellable = true)
    private void stackableStew(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        // >= 1 because it is decreased by 1 before our code execution
        if (ItemsHelper.isModified(stack) && stack.getCount() >= 1) {
            if (user instanceof PlayerEntity) {
                ItemsHelper.insertNewItem((PlayerEntity) user, new ItemStack(Items.BOWL));
            }
            cir.setReturnValue(stack);
        }
    }
}
