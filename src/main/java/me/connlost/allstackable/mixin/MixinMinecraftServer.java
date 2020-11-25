package me.connlost.allstackable.mixin;

import me.connlost.allstackable.server.Server;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Inject(method = "loadWorld", at = @At("RETURN"))
    private void serverLoaded(CallbackInfo ci)
    {
        Server.onServerLoaded((MinecraftServer) (Object) this);
    }
}
