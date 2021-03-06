package dev.fxcte.creepyware.features.modules.misc;

import dev.fxcte.creepyware.event.events.PacketEvent;
import dev.fxcte.creepyware.features.command.Command;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.modules.client.Managers;
import dev.fxcte.creepyware.features.setting.Setting;
import dev.fxcte.creepyware.util.TextUtil;
import dev.fxcte.creepyware.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public
class ChatModifier
        extends Module {
    private static ChatModifier INSTANCE = new ChatModifier();
    private final Timer timer = new Timer();
    public Setting <Suffix> suffix = this.register(new Setting <>("Suffix", Suffix.NONE, "Your Suffix."));
    public Setting <Boolean> clean = this.register(new Setting <>("CleanChat", false, "Cleans your chat"));
    public Setting <Boolean> infinite = this.register(new Setting <>("Infinite", false, "Makes your chat infinite."));
    public Setting <Boolean> autoQMain = this.register(new Setting <>("AutoQMain", false, "Spams AutoQMain"));
    public Setting <Boolean> qNotification = this.register(new Setting <Object>("QNotification", false, v -> this.autoQMain.getValue()));
    public Setting <Integer> qDelay = this.register(new Setting <Object>("QDelay", 9, 1, 90, v -> this.autoQMain.getValue()));
    public Setting <TextUtil.Color> timeStamps = this.register(new Setting <>("Speed", "Time", 0.0, 0.0, TextUtil.Color.NONE, 0));
    public Setting <Boolean> rainbowTimeStamps = this.register(new Setting <Object>("RainbowTimeStamps", false, v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public Setting <TextUtil.Color> bracket = this.register(new Setting <Object>("Bracket", TextUtil.Color.WHITE, v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public Setting <Boolean> space = this.register(new Setting <Object>("Space", true, v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public Setting <Boolean> all = this.register(new Setting <Object>("All", false, v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public Setting <Boolean> shrug = this.register(new Setting <>("Speed", "Shrug", 0.0, 0.0, false, 0));

    public
    ChatModifier() {
        super("ChatModifier", "Modifies your chat", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static
    ChatModifier getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatModifier();
        }
        return INSTANCE;
    }

    private
    void setInstance() {
        INSTANCE = this;
    }

    @Override
    public
    void onUpdate() {
        if (this.shrug.getValue()) {
            ChatModifier.mc.player.sendChatMessage(TextUtil.shrug);
            this.shrug.setValue(false);
        }
        if (this.autoQMain.getValue()) {
            if (! this.shouldSendMessage(ChatModifier.mc.player)) {
                return;
            }
            if (this.qNotification.getValue()) {
                Command.sendMessage("<AutoQueueMain> Sending message: /queue main");
            }
            ChatModifier.mc.player.sendChatMessage("/queue main");
            this.timer.reset();
        }
    }

    @SubscribeEvent
    public
    void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage) {
            CPacketChatMessage packet = event.getPacket();
            String s = packet.getMessage();
            if (s.startsWith("/") || s.startsWith("!")) {
                return;
            }
            switch (this.suffix.getValue()) {
                case Creepyware: {
                    s = s + " \u23d0 creepyware";
                    break;
                }

            }
            if (s.length() >= 256) {
                s = s.substring(0, 256);
            }
            packet.message = s;
        }
    }

    @SubscribeEvent
    public
    void onChatPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() != 0 || event.getPacket() instanceof SPacketChat) {
            // empty if block
        }
    }

    @SubscribeEvent
    public
    void onPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() == 0 && this.timeStamps.getValue() != TextUtil.Color.NONE && event.getPacket() instanceof SPacketChat) {
            if (! ((SPacketChat) event.getPacket()).isSystem()) {
                return;
            }
            String originalMessage = ((SPacketChat) event.getPacket()).chatComponent.getFormattedText();
            String message = this.getTimeString(originalMessage) + originalMessage;
            ((SPacketChat) event.getPacket()).chatComponent = new TextComponentString(message);
        }
    }

    public
    String getTimeString(String message) {
        String date = new SimpleDateFormat("k:mm").format(new Date());
        if (this.rainbowTimeStamps.getValue()) {
            String timeString = "<" + date + ">" + (this.space.getValue() ? " " : "");
            StringBuilder builder = new StringBuilder(timeString);
            builder.insert(0, "\u00a7+");
            if (! message.contains(Managers.getInstance().getRainbowCommandMessage())) {
                builder.append("\u00a7r");
            }
            return builder.toString();
        }
        return (this.bracket.getValue() == TextUtil.Color.NONE ? "" : TextUtil.coloredString("<", this.bracket.getValue())) + TextUtil.coloredString(date, this.timeStamps.getValue()) + (this.bracket.getValue() == TextUtil.Color.NONE ? "" : TextUtil.coloredString(">", this.bracket.getValue())) + (this.space.getValue() ? " " : "") + "\u00a7r";
    }

    private
    boolean shouldSendMessage(EntityPlayer player) {
        if (player.dimension != 1) {
            return false;
        }
        if (! this.timer.passedS(this.qDelay.getValue())) {
            return false;
        }
        return player.getPosition().equals(new Vec3i(0, 240, 0));
    }

    public
    enum Suffix {
        NONE,
        Creepyware

    }
}

