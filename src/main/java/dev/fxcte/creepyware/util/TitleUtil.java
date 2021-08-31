package dev.fxcte.creepyware.util;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.Display;

public class TitleUtil {
    int ticks = 0;
    int bruh = 0;
    int breakTimer = 0;
    String bruh1 = "Cr33pyWare | b0.1.6";
    boolean qwerty = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        ++this.ticks;
        if (this.ticks % 17 == 0) {
            Display.setTitle((String)this.bruh1.substring(0, this.bruh1.length() - this.bruh));
            if (this.bruh == this.bruh1.length() && this.breakTimer != 0 || this.bruh == 0 && this.breakTimer != 0) {
                ++this.breakTimer;
                return;
            }
            this.breakTimer = 0;
            if (this.bruh == this.bruh1.length()) {
                this.qwerty = true;
            }
            this.bruh = this.qwerty ? --this.bruh : ++this.bruh;
            if (this.bruh == 0) {
                this.qwerty = false;
            }
        }
    }
}

