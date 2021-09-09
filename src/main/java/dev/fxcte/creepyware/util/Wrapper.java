package dev.fxcte.creepyware.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;

import javax.annotation.Nullable;

public
class Wrapper {
    public static final Minecraft mc;

    static {
        mc = Minecraft.getMinecraft();
    }

    @Nullable
    public static
    EntityPlayerSP getPlayer() {
        return Wrapper.mc.player;
    }

    @Nullable
    public static
    WorldClient getWorld() {
        return Wrapper.mc.world;
    }

    public static
    FontRenderer getFontRenderer() {
        return Wrapper.mc.fontRenderer;
    }
}