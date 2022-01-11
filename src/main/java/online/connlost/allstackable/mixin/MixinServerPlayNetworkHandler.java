package online.connlost.allstackable.mixin;

import online.connlost.allstackable.util.ItemsHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
    @Shadow 
    public ServerPlayerEntity player;
    
    /**
    * 
    * Set the size of the new signed books to the size of the old writable books
    * instead of only making one new signed book regardless of the stack size
    * This prevents unexpected item deletion.
    * 
    **/
    @ModifyVariable(method = "addBook", at = @At("STORE"), ordinal = 1)
    public ItemStack fixSignedBookCount(ItemStack itemStack2, TextStream.Message title, List<TextStream.Message> pages, int slotId) {
        if (ItemsHelper.isModified(itemStack2)) { 
            itemStack2.setCount(player.getInventory().getStack(slotId).getCount());
        }
        return itemStack2;
    }
}
