package dev.fxcte.creepyware.mixin;

import dev.fxcte.creepyware.CreepyWare;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class CreepywareMixinLoader
implements IFMLLoadingPlugin {
    private static boolean isObfuscatedEnvironment = false;

    public CreepywareMixinLoader() {
        CreepyWare.LOGGER.info("CreepyWare mixins initialized");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.creepyware.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        CreepyWare.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
        isObfuscatedEnvironment = (Boolean)data.get("runtimeDeobfuscationEnabled");
    }

    public String getAccessTransformerClass() {
        return null;
    }
}

