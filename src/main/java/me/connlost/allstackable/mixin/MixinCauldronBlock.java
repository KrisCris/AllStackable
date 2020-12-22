package me.connlost.allstackable.mixin;

import me.connlost.allstackable.util.ItemsHelper;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.connlost.allstackable.util.ItemsHelper.insertNewItem;

@Mixin(CauldronBlock.class)
public class MixinCauldronBlock {

    @Shadow
    public void setLevel(World world, BlockPos pos, BlockState state, int level) {
    }

    @Shadow
    public static final IntProperty LEVEL = Properties.LEVEL_3;

    //tried to simply redirect the setStackInHand method... But failed and no idea why.
    @Inject(method = "activate", at = @At(value = "HEAD"), cancellable = true)
    private void fixMojangStupidity(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (ItemsHelper.isModified(itemStack)) {
            if (itemStack.isEmpty()) {
                cir.setReturnValue(true);
            } else {
                int i = (Integer) state.get(LEVEL);
                Item item = itemStack.getItem();

                if (item == Items.WATER_BUCKET) {
                    if (i < 3 && !world.isClient) {
                        if (!player.abilities.creativeMode) {
                            itemStack.decrement(1);
                        }
                        insertNewItem(player, hand, itemStack, new ItemStack(Items.BUCKET, 1));
                        player.incrementStat(Stats.FILL_CAULDRON);
                        setLevel(world, pos, state, 3);
                        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                    cir.setReturnValue(true);
                } else if (item == Items.POTION && PotionUtil.getPotion(itemStack) == Potions.WATER) {
                    if (i < 3 && !world.isClient) {
                        if (!player.abilities.creativeMode) {
                            player.incrementStat(Stats.USE_CAULDRON);
                            itemStack.decrement(1);
                            insertNewItem(player, hand, itemStack, new ItemStack(Items.GLASS_BOTTLE, 1));
                            if (player instanceof ServerPlayerEntity) {
                                ((ServerPlayerEntity) player).openContainer(player.playerContainer);
                            }
                        }
                        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        setLevel(world, pos, state, i + 1);
                    }
                    cir.setReturnValue(true);
                } else if (i > 0 && item instanceof BlockItem) {
                    Block block = ((BlockItem) item).getBlock();
                    if (block instanceof ShulkerBoxBlock && !world.isClient()) {
                        ItemStack itemStack5 = new ItemStack(Blocks.SHULKER_BOX, 1);
                        if (itemStack.hasTag()) {
                            itemStack5.setTag(itemStack.getTag().copy());
                        }

                        itemStack.decrement(1);
                        insertNewItem(player, hand, itemStack, itemStack5);
                        setLevel(world, pos, state, i - 1);
                        player.incrementStat(Stats.CLEAN_SHULKER_BOX);
                        cir.setReturnValue(true);
                    }
                }
            }


        }


    }


}

