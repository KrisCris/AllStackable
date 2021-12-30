package online.connlost.allstackable.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import online.connlost.allstackable.util.ItemsHelper;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public class MixinAbstractFurnaceBlockEntity {
    @Inject(method = "tick", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private static void popBuckets(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci){
        ItemStack itemStack = (ItemStack)((AccessorFurnaceInventory)blockEntity).getInventory().get(1);
        if(ItemsHelper.isModified(itemStack)) {
            if (itemStack.isOf(Items.LAVA_BUCKET)){
                ItemScatterer.spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), new ItemStack(Items.BUCKET));
            }
        }
    }
}
