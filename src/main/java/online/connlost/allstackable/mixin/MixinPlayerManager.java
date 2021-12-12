package online.connlost.allstackable.mixin;

import online.connlost.allstackable.server.Server;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerJoined(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci)
    {
        Server.onPlayerJoin(player);
    }
}
