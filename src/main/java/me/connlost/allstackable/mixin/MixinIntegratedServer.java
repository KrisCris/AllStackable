package me.connlost.allstackable.mixin;

import me.connlost.allstackable.server.Server;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IntegratedServer.class)
public class MixinIntegratedServer {
    //integrated only
    @Inject(method = "loadWorld", at = @At("RETURN"))
    private void serverLoaded(CallbackInfo ci)
    {
        Server.onServerLoaded((MinecraftServer) (Object) this);
    }
}
