package online.connlost.allstackable.mixin;

import online.connlost.allstackable.util.IItemMaxCount;
import online.connlost.allstackable.util.ItemsHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public class MixinAnvilScreenHandler {
    /**
    * 
    * Decrement the input stacks by one instead of blindly setting them
    * to an empty stack. The decrement is only done on the server to avoid
    * a desync between the server and client that results in renaming breaking.
    * This prevents entire stacks from being deleted at a time.
    * 
    **/
    @Redirect(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V"))
    private void decrementOne(Inventory inventory, int slot, ItemStack stack, PlayerEntity player, ItemStack takenStack) {
        ItemStack originalStack = inventory.getStack(slot);
        if (ItemsHelper.isModified(originalStack) && originalStack.getCount() > 1) {
            if(stack.isEmpty()) {
                stack = originalStack;
                if (!player.getWorld().isClient) {
                    stack.decrement(1);
                }
            }
        }
            
        inventory.setStack(slot, stack);
    }
    
    /**
    * 
    * Update the output every time the output is taken in addition to
    * updating when the inputs are changed. 
    * This allows the output to be taken multiple times without needing
    * to change an input in between every take.
    * 
    **/
    @Inject(method = "onTakeOutput", at = @At("RETURN"))
    private void updateAfterTaking(CallbackInfo ci) {
        ((AnvilScreenHandler)(Object)this).updateResult();
    }
    
    /**
    * 
    * Only output a single item at a time when the item was originally capped
    * at 1.
    * This prevents entire stacks from being repaired/enchanted at a time with
    * wrong costs. It assumes that if the item was originally stackable, that
    * its anvil costs are already balanced.
    *
    **/
    @ModifyVariable(method = "updateResult", at = @At("STORE"), ordinal = 0)
    private ItemStack copyOne(ItemStack stack) {
        if (ItemsHelper.isModified(stack) && ((IItemMaxCount) stack.getItem()).getVanillaMaxCount() == 1) { 
            stack = stack.copy();
            stack.setCount(1);
        }
        return stack;
    }
}
