package dev.fxcte.creepyware;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import dev.fxcte.creepyware.features.modules.misc.RPC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

public class DiscordPresence {
    public static DiscordRichPresence presence;
    private static final DiscordRPC rpc;
    private static Thread thread;
    private static int index;

    public static void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("866117154675097600", handlers, true, "");
        DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;
        DiscordPresence.presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu ? "Main menu" : "Playing " + (Minecraft.getMinecraft().currentServerData != null ? (RPC.INSTANCE.showIP.getValue().booleanValue() ? "on " + Minecraft.getMinecraft().currentServerData.serverIP + "." : " multiplayer") : " singleplayer");
        DiscordPresence.presence.state = "Donbass activity";
        DiscordPresence.presence.largeImageKey = "creepy";
        DiscordPresence.presence.largeImageText = "b0.2.0";
        DiscordPresence.presence.smallImageKey = "logo";
        rpc.Discord_UpdatePresence(presence);
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                rpc.Discord_RunCallbacks();
                DiscordPresence.presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu ? "Main menu" : "Playing " + (Minecraft.getMinecraft().currentServerData != null ? (RPC.INSTANCE.showIP.getValue().booleanValue() ? "on " + Minecraft.getMinecraft().currentServerData.serverIP + "." : " multiplayer") : " singleplayer");
                DiscordPresence.presence.state = "Eating kids";
                if (RPC.INSTANCE.users.getValue().booleanValue()) {
                    if (index == 6) {
                        index = 1;
                    }
                    DiscordPresence.presence.smallImageKey = "user" + index;
                    if (++index == 2) {
                        DiscordPresence.presence.smallImageText = "BlackBro4";
                    }
                    if (index == 3) {
                        DiscordPresence.presence.smallImageText = "rianix";
                    }
                    if (index == 4) {
                        DiscordPresence.presence.smallImageText = "Sudmarin";
                    }
                    if (index == 5) {
                        DiscordPresence.presence.smallImageText = "Ziasan";
                    }
                }
                rpc.Discord_UpdatePresence(presence);
                try {
                    Thread.sleep(2000L);
                }
                catch (InterruptedException interruptedException) {}
            }
        }, "RPC-Callback-Handler");
        thread.start();
    }

    public static void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        rpc.Discord_Shutdown();
    }

    static {
        index = 1;
        rpc = DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence();
    }
}

