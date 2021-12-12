package online.connlost.allstackable.mixin;

import online.connlost.allstackable.util.ItemsHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SuspiciousStewItem.class)
public class MixinSuspiciousStewItem {

    @Inject(method = "finishUsing", at = @At(value = "NEW", target = "net/minecraft/item/ItemStack"), cancellable = true)
    private void stackableStew(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (ItemsHelper.isModified(stack) && stack.getCount() > 0) {
            if (user instanceof PlayerEntity) {
                ItemsHelper.insertNewItem((PlayerEntity)user, new ItemStack(Items.BOWL));
            }
            cir.setReturnValue(stack);
        }
    }
}
