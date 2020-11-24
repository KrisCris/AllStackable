package me.connlost.allstackable.mixin;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Deprecated
@Mixin(ScreenHandler.class)
public class MixinScreenHandler {

    @Redirect(
            method = "insertItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/slot/Slot;getMaxItemCount()I",
                    ordinal = 0
            )
    )
    private int getToolsItemCount(Slot slot, ItemStack stack){
        Item stackItem = stack.getItem();
        System.out.println(stackItem);
        if (stackItem instanceof ToolItem || stackItem instanceof ArmorItem){
            System.out.println("meh");
            return 1;
        } else {
            return slot.getMaxItemCount();
        }
    }
}
