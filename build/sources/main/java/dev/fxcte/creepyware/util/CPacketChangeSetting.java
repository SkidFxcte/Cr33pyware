package dev.fxcte.creepyware.util;

import dev.fxcte.creepyware.CreepyWare;
import dev.fxcte.creepyware.event.events.ValueChangeEvent;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.common.MinecraftForge;

import java.io.IOException;

public class CPacketChangeSetting
        implements Packet<INetHandlerPlayServer> {
    public String setting;

    public CPacketChangeSetting(String module, String setting, String value) {
        this.setting = setting + "-" + module + "-" + value;
    }

    public CPacketChangeSetting(Module module, Setting setting, String value) {
        this.setting = setting.getName() + "-" + module.getName() + "-" + value;
    }

    public void readPacketData(PacketBuffer buf) throws IOException {
        this.setting = buf.readString(256);
    }

    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(this.setting);
    }

    public void processPacket(INetHandlerPlayServer handler) {
        Module module = CreepyWare.moduleManager.getModuleByName(this.setting.split("-")[1]);
        Setting setting1 = module.getSettingByName(this.setting.split("-")[0]);
        MinecraftForge.EVENT_BUS.post(new ValueChangeEvent(setting1, this.setting.split("-")[2]));
    }
}

