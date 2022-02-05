package online.connlost.allstackable.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import online.connlost.allstackable.util.ItemsHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PotionItem.class)
public class MixinPotionItem {
    @Redirect(method = "finishUsing", at = @At(value="INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z"))
    public boolean dropWhenInventoryFull(PlayerInventory instance, ItemStack stack){
        ItemsHelper.insertNewItem(instance.player, new ItemStack(Items.GLASS_BOTTLE));
        return true;
    }
}
