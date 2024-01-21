package online.connlost.allstackable.mixin;

import online.connlost.allstackable.util.ItemsHelper;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractCauldronBlock.class)
public class MixinCauldronBlock {

    @Shadow
    CauldronBehavior.CauldronBehaviorMap behaviorMap;


    CauldronBehavior CLEAN_STACKED_SHULKER_BOX = (state, world, pos, player, hand, stack) -> {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (!(block instanceof ShulkerBoxBlock)) {
            return ActionResult.PASS;
        } else {
            if (!world.isClient) {
                ItemStack itemStack = new ItemStack(Blocks.SHULKER_BOX);
                if (stack.hasNbt()) {
                    itemStack.setNbt(stack.getNbt().copy());
                }
                ItemsHelper.insertNewItem(player, itemStack);
                stack.decrement(1);
                player.incrementStat(Stats.CLEAN_SHULKER_BOX);
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
            }

            return ActionResult.success(world.isClient);
        }
    };

    @Inject(method = "onUse", at=@At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"), cancellable = true)
    private void cleanStackedShulkerBox(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir){
        ItemStack itemStack = player.getStackInHand(hand);
        if (ItemsHelper.isModified(itemStack) && itemStack.getCount() > 1) {
            if(behaviorMap.map().get(itemStack.getItem()) == CauldronBehavior.CLEAN_SHULKER_BOX){
                cir.setReturnValue(CLEAN_STACKED_SHULKER_BOX.interact(state, world, pos, player, hand, itemStack));
            }
        }
    }
}

