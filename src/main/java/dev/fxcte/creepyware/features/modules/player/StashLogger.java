package dev.fxcte.creepyware.features.modules.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.fxcte.creepyware.event.events.PacketEvent;
import dev.fxcte.creepyware.features.command.Command;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

public class StashLogger
        extends Module {
    private final Setting<Boolean> chests = this.register(new Setting <> ("Speed" , "Chests" , 0.0 , 0.0 , true , 0));
    private final Setting<Integer> chestsValue = this.register(new Setting<Object>("ChestsValue", 4 , 1 , 30 , v -> this.chests.getValue()));
    private final Setting<Boolean> Shulkers = this.register(new Setting <> ("Speed" , "Shulkers" , 0.0 , 0.0 , true , 0));
    private final Setting<Integer> shulkersValue = this.register(new Setting<Object>("ShulkersValue", 4 , 1 , 30 , v -> this.Shulkers.getValue()));
    private final Setting<Boolean> writeToFile = this.register(new Setting <> ("Speed" , "CoordsSaver" , 0.0 , 0.0 , true , 0));
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
            SPacketChunkData l_Packet = event.getPacket();
            int l_ChestsCount = 0;
            int shulkers = 0;
            for (NBTTagCompound l_Tag : l_Packet.getTileEntityTags()) {
                String l_Id = l_Tag.getString("id");
                if (l_Id.equals("minecraft:chest") && this.chests.getValue ()) {
                    ++l_ChestsCount;
                    continue;
                }
                if (!l_Id.equals("minecraft:shulker_box") || ! this.Shulkers.getValue ()) continue;
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
        String string = server = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer".toUpperCase() : Objects.requireNonNull (StashLogger.mc.getCurrentServerData ()).serverIP;
        if (this.writeToFile.getValue () && save) {
            try {
                FileWriter writer = new FileWriter(this.mainFolder + "/stashes.txt", true);
                writer.write("[" + server + "]: " + message + "\n");
                writer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP , 1.0f , 1.0f));
        Command.sendMessage(ChatFormatting.GREEN + message);
    }
}

