  package me.alpha432.oyvey.features.modules.misc;

  import me.alpha432.oyvey.OyVey;
  import me.alpha432.oyvey.event.events.PacketEvent;
  import me.alpha432.oyvey.features.modules.Module;
  import me.alpha432.oyvey.features.setting.Setting;
  import net.minecraft.network.play.client.CPacketChatMessage;
  import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

  public class ChatSuffix extends Module {
    public Boolean suffix = true;
    public String s;

    public ChatSuffix() {
        super("ChatSuffix", "show you're cool.", Module.Category.MISC, true, false, false);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
      if ( event.getStage ( ) == 0 && event.getPacket ( ) instanceof CPacketChatMessage ) {
        CPacketChatMessage packet = event.getPacket ( );
        String s = packet.getMessage ( );
        if ( s.startsWith ( "/" ) ) {
          return;
        }
        s = s + " 》ᴄʀᴇᴇᴘʏᴡᴀʀᴇ";
        if ( s.length ( ) >= 256 ) {
          s = s.substring ( 0 , 256 );
        }
        packet.message = s;
      }
    }

    @SubscribeEvent
    public
    void onChatPacketReceive ( PacketEvent.Receive event ) {
        if ( event.getStage ( ) == 0 ) {
            event.getPacket ( );
        }// empty if block
    }
  }
