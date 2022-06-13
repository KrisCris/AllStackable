package online.connlost.allstackable.mixin;

import net.minecraft.server.filter.FilteredMessage;
import online.connlost.allstackable.util.ItemsHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
    @Shadow
    public ServerPlayerEntity player;

    /**
    *
    * Set the size of the new signed books to the size of the old writable books
    * instead of only making one new signed book regardless of the stack size.
    * This prevents unexpected item deletion.
    *
    **/
    @ModifyVariable(method = "addBook", at = @At("STORE"), ordinal = 1)
    public ItemStack fixSignedBookCount(ItemStack itemStack2, FilteredMessage<String> title, List<FilteredMessage<String>> pages, int slotId) {
        ItemStack originalStack = player.getInventory().getStack(slotId);
        if (ItemsHelper.isModified(originalStack)) {
            itemStack2.setCount(originalStack.getCount());
        }
        return itemStack2;
    }

    /**
    *
    * Split the written book into several stacks if it is over its maximum
    * stack count. This will occur whenever the writable book stack count is
    * greater than the written book's maximum stack count.
    *
    **/
    @Inject(method = "addBook", at = @At("RETURN"))
    public void fixSignedBookOverCount(FilteredMessage<String> title, List<FilteredMessage<String>> pages, int slotId, CallbackInfo ci) {
        ItemStack itemStack2 = player.getInventory().getStack(slotId);
        if (ItemsHelper.isModified(itemStack2) && (itemStack2.getCount() > itemStack2.getMaxCount())) {
            ItemStack splitStack = itemStack2.copy();
            int count = itemStack2.getCount() % itemStack2.getMaxCount();
            splitStack.setCount(count);
            itemStack2.decrement(count);
            ItemsHelper.insertNewItem(player, splitStack);
            while(itemStack2.getCount() > itemStack2.getMaxCount()) {
                splitStack = itemStack2.copy();
                count = itemStack2.getMaxCount();
                splitStack.setCount(count);
                itemStack2.decrement(count);
                ItemsHelper.insertNewItem(player, splitStack);
            }
        }
    }
}
