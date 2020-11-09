package me.connlost.allstackable.mixin;

import me.connlost.allstackable.server.config.ConfigManager;
import me.connlost.allstackable.util.ItemsHelper;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Deprecated
@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {
    public MixinItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "canMerge()Z", at = @At("HEAD"), cancellable = true)
    private void cantMergeItemSBox(CallbackInfoReturnable<Boolean> cir) {
        if (!ConfigManager.allowItemShulkerStack){
            ItemEntity self = (ItemEntity) (Object) this;
            ItemStack selfStack = self.getStack();
            if ((selfStack.getItem() instanceof BlockItem) && (((BlockItem) selfStack.getItem()).getBlock() instanceof ShulkerBoxBlock)) {
                if (ItemsHelper.shulkerBoxHasItems(selfStack)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "canMerge(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private static void cantMergeItemSBox(ItemStack stack1, ItemStack stack2, CallbackInfoReturnable<Boolean> cir) {
        if (!ConfigManager.allowItemShulkerStack){
            if (
                    (stack1.getItem() instanceof BlockItem) &&
                            (((BlockItem) stack1.getItem()).getBlock() instanceof ShulkerBoxBlock) &&
                            (stack2.getItem() instanceof BlockItem) &&
                            (((BlockItem) stack2.getItem()).getBlock() instanceof ShulkerBoxBlock)
            ) {
                if (ItemsHelper.shulkerBoxHasItems(stack1) || ItemsHelper.shulkerBoxHasItems(stack2)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }


}
