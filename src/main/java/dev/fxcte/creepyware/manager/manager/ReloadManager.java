package dev.fxcte.creepyware.manager;

import dev.fxcte.creepyware.CreepyWare;
import dev.fxcte.creepyware.event.events.PacketEvent;
import dev.fxcte.creepyware.features.Feature;
import dev.fxcte.creepyware.features.command.Command;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReloadManager
        extends Feature {
    public String prefix;

    public void init(String prefix) {
        this.prefix = prefix;
        MinecraftForge.EVENT_BUS.register(this);
        if (!ReloadManager.fullNullCheck()) {
            Command.sendMessage("\u00a7cCreepyWare has been unloaded. Type " + prefix + "reload to reload.");
        }
    }

    public void unload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        CPacketChatMessage packet;
        if (event.getPacket() instanceof CPacketChatMessage && (packet = event.getPacket()).getMessage().startsWith(this.prefix) && packet.getMessage().contains("reload")) {
            CreepyWare.load();
            event.setCanceled(true);
        }
    }
}

