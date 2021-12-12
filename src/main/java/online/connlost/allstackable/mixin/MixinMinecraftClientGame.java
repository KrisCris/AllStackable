package online.connlost.allstackable.mixin;

import online.connlost.allstackable.client.ConfigSync;
import net.minecraft.client.MinecraftClientGame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClientGame.class)
public abstract class MixinMinecraftClientGame {
    @Inject(method = "onLeaveGameSession", at=@At("RETURN"))
    private void resetMaxCount(CallbackInfo ci){
        ConfigSync.resetConfig();
    }
}
