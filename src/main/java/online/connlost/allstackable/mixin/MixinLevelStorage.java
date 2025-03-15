package online.connlost.allstackable.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.file.Path;
import static online.connlost.allstackable.server.Server.config_manager;


@Mixin(LevelStorage.class)
public class MixinLevelStorage {
    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorage;resolve(Ljava/lang/String;)Ljava/nio/file/Path;"), method = "createSession")
    private Path initConfig(LevelStorage instance, String name, Operation<Path> original){
        Path path = original.call(instance, name);
        config_manager.passConfigFile(path.resolve("allstackable-config.json").toFile());
        config_manager.setupConfig();
        return path;
    }
}
