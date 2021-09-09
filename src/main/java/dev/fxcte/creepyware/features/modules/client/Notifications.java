package dev.fxcte.creepyware.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.fxcte.creepyware.CreepyWare;
import dev.fxcte.creepyware.event.events.ClientEvent;
import dev.fxcte.creepyware.event.events.PacketEvent;
import dev.fxcte.creepyware.features.command.Command;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;
import dev.fxcte.creepyware.manager.FileManager;
import dev.fxcte.creepyware.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public
class Notifications
        extends Module {
    private static final String fileName = "creepyware/util/ModuleMessage_List.txt";
    private static final List <String> modules = new ArrayList <>();
    private static Notifications INSTANCE = new Notifications();
    private final Timer timer = new Timer();
    public Setting <Boolean> totemPops = this.register(new Setting <>("Speed", "TotemPops", 0.0, 0.0, false, 0));
    public Setting <Boolean> totemNoti = this.register(new Setting <Object>("TotemNoti", true, v -> this.totemPops.getValue()));
    public Setting <Integer> delay = this.register(new Setting <Object>("Delay", 2000, 0, 5000, v -> this.totemPops.getValue(), "Delays messages."));
    public Setting <Boolean> clearOnLogout = this.register(new Setting <>("Speed", "LogoutClear", 0.0, 0.0, false, 0));
    public Setting <Boolean> moduleMessage = this.register(new Setting <>("Speed", "ModuleMessage", 0.0, 0.0, false, 0));
    private final Setting <Boolean> readfile = this.register(new Setting <Object>("LoadFile", false, v -> this.moduleMessage.getValue()));
    public Setting <Boolean> list = this.register(new Setting <Object>("List", false, v -> this.moduleMessage.getValue()));
    public Setting <Boolean> watermark = this.register(new Setting <Object>("Watermark", true, v -> this.moduleMessage.getValue()));
    public Setting <Boolean> visualRange = this.register(new Setting <>("Speed", "VisualRange", 0.0, 0.0, false, 0));
    public Setting <Boolean> VisualRangeSound = this.register(new Setting <>("Speed", "VisualRangeSound", 0.0, 0.0, false, 0));
    public Setting <Boolean> coords = this.register(new Setting <Object>("Coords", true, v -> this.visualRange.getValue()));
    public Setting <Boolean> leaving = this.register(new Setting <Object>("Leaving", false, v -> this.visualRange.getValue()));
    public Setting <Boolean> pearls = this.register(new Setting <>("Speed", "PearlNotifs", 0.0, 0.0, false, 0));
    public Setting <Boolean> crash = this.register(new Setting <>("Speed", "Crash", 0.0, 0.0, false, 0));
    public Setting <Boolean> popUp = this.register(new Setting <>("Speed", "PopUpVisualRange", 0.0, 0.0, false, 0));
    public Timer totemAnnounce = new Timer();
    private List <EntityPlayer> knownPlayers = new ArrayList <>();
    private boolean check;

    public
    Notifications() {
        super("Notifications", "Sends Messages.", Module.Category.CLIENT, true, false, false);
        this.setInstance();
    }

    public static
    Notifications getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Notifications();
        }
        return INSTANCE;
    }

    public static
    void displayCrash(Exception e) {
        Command.sendMessage("\u00a7cException caught: " + e.getMessage());
    }

    private
    void setInstance() {
        INSTANCE = this;
    }

    @Override
    public
    void onLoad() {
        this.check = true;
        this.loadFile();
        this.check = false;
    }

    @Override
    public
    void onEnable() {
        this.knownPlayers = new ArrayList <>();
        if (! this.check) {
            this.loadFile();
        }
    }

    @Override
    public
    void onUpdate() {
        if (this.readfile.getValue()) {
            if (! this.check) {
                Command.sendMessage("Loading File...");
                this.timer.reset();
                this.loadFile();
            }
            this.check = true;
        }
        if (this.check && this.timer.passedMs(750L)) {
            this.readfile.setValue(false);
            this.check = false;
        }
        if (this.visualRange.getValue()) {
            ArrayList <EntityPlayer> tickPlayerList = new ArrayList <>(Notifications.mc.world.playerEntities);
            if (tickPlayerList.size() > 0) {
                for (EntityPlayer player : tickPlayerList) {
                    if (player.getName().equals(Notifications.mc.player.getName()) || this.knownPlayers.contains(player))
                        continue;
                    this.knownPlayers.add(player);
                    if (CreepyWare.friendManager.isFriend(player)) {
                        Command.sendMessage("Player \u00a7a" + player.getName() + "\u00a7r" + " entered your visual range" + (this.coords.getValue() ? " at (" + (int) player.posX + ", " + (int) player.posY + ", " + (int) player.posZ + ")!" : "!"), this.popUp.getValue());
                    } else {
                        Command.sendMessage("Player \u00a7c" + player.getName() + "\u00a7r" + " entered your visual range" + (this.coords.getValue() ? " at (" + (int) player.posX + ", " + (int) player.posY + ", " + (int) player.posZ + ")!" : "!"), this.popUp.getValue());
                    }
                    if (this.VisualRangeSound.getValue()) {
                        Notifications.mc.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
                    }
                    return;
                }
            }
            if (this.knownPlayers.size() > 0) {
                for (EntityPlayer player : this.knownPlayers) {
                    if (tickPlayerList.contains(player)) continue;
                    this.knownPlayers.remove(player);
                    if (this.leaving.getValue()) {
                        if (CreepyWare.friendManager.isFriend(player)) {
                            Command.sendMessage("Player \u00a7a" + player.getName() + "\u00a7r" + " left your visual range" + (this.coords.getValue() ? " at (" + (int) player.posX + ", " + (int) player.posY + ", " + (int) player.posZ + ")!" : "!"), this.popUp.getValue());
                        } else {
                            Command.sendMessage("Player \u00a7c" + player.getName() + "\u00a7r" + " left your visual range" + (this.coords.getValue() ? " at (" + (int) player.posX + ", " + (int) player.posY + ", " + (int) player.posZ + ")!" : "!"), this.popUp.getValue());
                        }
                    }
                    return;
                }
            }
        }
    }

    public
    void loadFile() {
        List <String> fileInput = FileManager.readTextFileAllLines(fileName);
        Iterator <String> i = fileInput.iterator();
        modules.clear();
        while (i.hasNext()) {
            String s = i.next();
            if (s.replaceAll("\\s", "").isEmpty()) continue;
            modules.add(s);
        }
    }

    @SubscribeEvent
    public
    void onReceivePacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject && this.pearls.getValue()) {
            SPacketSpawnObject packet = event.getPacket();
            EntityPlayer player = Notifications.mc.world.getClosestPlayer(packet.getX(), packet.getY(), packet.getZ(), 1.0, false);
            if (player == null) {
                return;
            }
            if (packet.getEntityID() == 85) {
                Command.sendMessage("\u00a7cPearl thrown by " + player.getName() + " at X:" + (int) packet.getX() + " Y:" + (int) packet.getY() + " Z:" + (int) packet.getZ());
            }
        }
    }


    public
    TextComponentString getNotifierOn(Module module) {
        if (ModuleTools.getInstance().isEnabled()) {
            switch (ModuleTools.getInstance().notifier.getValue()) {
                case FUTURE: {
                    return new TextComponentString(ChatFormatting.RED + "[Future] " + ChatFormatting.GRAY + module.getDisplayName() + " toggled " + ChatFormatting.GREEN + "on" + ChatFormatting.GRAY + ".");
                }
                case PHOBOS: {
                    return new TextComponentString((CreepyWare.commandManager.getClientMessage()) + ChatFormatting.BOLD + module.getDisplayName() + ChatFormatting.RESET + ChatFormatting.GREEN + " enabled.");

                }
            }
        }
        return new TextComponentString(CreepyWare.commandManager.getClientMessage() + ChatFormatting.GREEN + module.getDisplayName() + " toggled on.");
    }

    public
    TextComponentString getNotifierOff(Module module) {
        if (ModuleTools.getInstance().isEnabled()) {
            switch (ModuleTools.getInstance().notifier.getValue()) {
                case FUTURE: {
                    return new TextComponentString(ChatFormatting.RED + "[Future] " + ChatFormatting.GRAY + module.getDisplayName() + " toggled " + ChatFormatting.RED + "off" + ChatFormatting.GRAY + ".");
                }
                case PHOBOS: {
                    return new TextComponentString((CreepyWare.commandManager.getClientMessage()) + ChatFormatting.BOLD + module.getDisplayName() + ChatFormatting.RESET + ChatFormatting.RED + " disabled.");

                }
            }
        }
        return new TextComponentString(CreepyWare.commandManager.getClientMessage() + ChatFormatting.RED + module.getDisplayName() + " toggled off.");
    }

    @SubscribeEvent
    public
    void onToggleModule(ClientEvent event) {
        int moduleNumber;
        Module module;
        if (! this.moduleMessage.getValue()) {
            return;
        }
        if (! (event.getStage() != 0 || (module = (Module) event.getFeature()).equals(this) || ! modules.contains(module.getDisplayName()) && this.list.getValue())) {
            moduleNumber = 0;
            for (char character : module.getDisplayName().toCharArray()) {
                moduleNumber += character;
                moduleNumber *= 10;
            }
            Notifications.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(getNotifierOff(module), moduleNumber);
        }

        if (event.getStage() == 1 && (modules.contains((module = (Module) event.getFeature()).getDisplayName()) || ! this.list.getValue())) {
            moduleNumber = 0;
            for (char character : module.getDisplayName().toCharArray()) {
                moduleNumber += character;
                moduleNumber *= 10;
            }
            Notifications.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(getNotifierOn(module), moduleNumber);
        }
    }
}
