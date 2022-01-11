package online.connlost.allstackable.mixin;

import online.connlost.allstackable.util.ItemsHelper;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(JukeboxBlockEntity.class)
public class MixinJukeboxBlockEntity {
    
    /**
    * 
    * Only store one record instead of the full stack, since that's what 
    * MusicDiscItem assumes.
    * This prevents item duplication where MusicDiscItem decrements the player's
    * stack by 1, but the Jukebox stores and drops the full stack instead.
    * 
    **/
    @ModifyVariable(method = "setRecord", at = @At("HEAD"))
    private ItemStack setRecord(ItemStack stack) {
        if (ItemsHelper.isModified(stack) && stack.getCount() > 1) { 
            stack.setCount(1);
        }
        return stack;
    }
}
