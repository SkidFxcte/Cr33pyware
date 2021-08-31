package dev.fxcte.creepyware.mixin;

import dev.fxcte.creepyware.CreepyWare;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

public class CreepyWareLoader
        implements IFMLLoadingPlugin {
    private static boolean isObfuscatedEnvironment = false;

    public CreepyWareLoader() {
        CreepyWare.LOGGER.info("\n\nLoading mixins by Alpha432");
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
        isObfuscatedEnvironment = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    public String getAccessTransformerClass() {
        return null;
    }
}

