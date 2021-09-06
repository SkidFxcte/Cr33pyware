package dev.fxcte.creepyware.features.modules.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import dev.fxcte.creepyware.event.events.PacketEvent;
import dev.fxcte.creepyware.features.command.Command;
import dev.fxcte.creepyware.features.setting.Setting;
import dev.fxcte.creepyware.features.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StashLogger
        extends Module {
    private final Setting<Boolean> chests = this.register(new Setting<Boolean>("Speed", true));
    private final Setting<Integer> chestsValue = this.register(new Setting<Object>("ChestsValue", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(30), v -> this.chests.getValue()));
    private final Setting<Boolean> Shulkers = this.register(new Setting<Boolean>("Speed", true));
    private final Setting<Integer> shulkersValue = this.register(new Setting<Object>("ShulkersValue", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(30), v -> this.Shulkers.getValue()));
    private final Setting<Boolean> writeToFile = this.register(new Setting<Boolean>("Speed", true));
    File mainFolder;
    final Iterator<NBTTagCompound> iterator;

    public StashLogger() {
        super("StashLogger", "Logs stashes", Module.Category.MISC, true, false, false);
        this.mainFolder = new File(Minecraft.getMinecraft().gameDir + File.separator + "legacy");
        this.iterator = null;
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (StashLogger.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketChunkData) {
            SPacketChunkData l_Packet = (SPacketChunkData)event.getPacket();
            int l_ChestsCount = 0;
            int shulkers = 0;
            for (NBTTagCompound l_Tag : l_Packet.getTileEntityTags()) {
                String l_Id = l_Tag.getString("id");
                if (l_Id.equals("minecraft:chest") && this.chests.getValue().booleanValue()) {
                    ++l_ChestsCount;
                    continue;
                }
                if (!l_Id.equals("minecraft:shulker_box") || !this.Shulkers.getValue().booleanValue()) continue;
                ++shulkers;
            }
            if (l_ChestsCount >= this.chestsValue.getValue()) {
                this.SendMessage(String.format("%s chests located at X: %s, Z: %s", l_ChestsCount, l_Packet.getChunkX() * 16, l_Packet.getChunkZ() * 16), true);
            }
            if (shulkers >= this.shulkersValue.getValue()) {
                this.SendMessage(String.format("%s shulker boxes at X: %s, Z: %s", shulkers, l_Packet.getChunkX() * 16, l_Packet.getChunkZ() * 16), true);
            }
        }
    }

    private void SendMessage(String message, boolean save) {
        String server;
        String string = server = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer".toUpperCase() : StashLogger.mc.getCurrentServerData().serverIP;
        if (this.writeToFile.getValue().booleanValue() && save) {
            try {
                FileWriter writer = new FileWriter(this.mainFolder + "/stashes.txt", true);
                writer.write("[" + server + "]: " + message + "\n");
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord((SoundEvent)SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, (float)1.0f, (float)1.0f));
        Command.sendMessage(ChatFormatting.GREEN + message);
    }
}

