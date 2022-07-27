package online.connlost.allstackable.mixin;

import net.fabricmc.loader.api.FabricLoader;
import online.connlost.allstackable.AllStackableInit;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;


public class AllStackableMixinsPlugin implements IMixinConfigPlugin {
    Logger LOGGER = AllStackableInit.LOGGER;

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        System.out.println(11111111);
            if (mixinClassName.equals("online.connlost.allstackable.mixin.MixinAnvilScreenHandler")) {
                boolean isCharmLoaded = FabricLoader.getInstance().isModLoaded("Charm");
                if (isCharmLoaded) {
                    LOGGER.info("[All Stackable][mixins] Charm is Loaded, disabling MixinAnvilScreenHandler (Anvil Fixes).");
                    return false;
                } else {
                    LOGGER.info("[All Stackable][mixins] Charm isn't Loaded, loading MixinAnvilScreenHandler (Anvil Fixes).");
                }
            }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
