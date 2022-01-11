package online.connlost.allstackable.mixin;

import online.connlost.allstackable.util.ItemsHelper;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public class MixinAnvilScreenHandler {
    /**
    * 
    * Decrement the input stacks by one instead of blindly setting them
    * to an empty stack.
    * This prevents entire stacks from being deleted at a time.
    * 
    **/
    @Redirect(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V"))
    private void decrementOne(Inventory inventory, int slot, ItemStack stack) {
        if (ItemsHelper.isModified(stack) && stack.getCount() > 1) { 
            if(stack.isEmpty()) {
                stack = inventory.getStack(slot);
                stack.decrement(1);
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
    * Only output a single item at a time.
    * This prevents entire stacks from being repaired/enchanted at a time.
    * 
    **/
    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;", ordinal = 0))
    public ItemStack copyOne(ItemStack itemStack) {
        ItemStack stack = itemStack.copy();
        if (ItemsHelper.isModified(stack) && stack.getCount() > 1) { 
            if(!stack.isEmpty()) {
                stack.setCount(1);
            }
        }
        return stack;
    }
}
