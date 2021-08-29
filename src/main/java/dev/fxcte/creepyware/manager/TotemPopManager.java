package me.alpha432.oyvey.manager;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.misc.PopCounter;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public
class TotemPopManager
        extends Feature {
    private final Set < EntityPlayer > toAnnounce = new HashSet <> ( );
    private PopCounter notifications;
    private Map < EntityPlayer, Integer > poplist = new ConcurrentHashMap <> ( );

    public
    void init ( ) {
        this.notifications = OyVey.moduleManager.getModuleByClass ( PopCounter.class );
    }


    public
    int getTotemPops ( EntityPlayer player ) {
        Integer pops = this.poplist.get ( player );
        if ( pops == null ) {
            return 0;
        }
        return pops;
    }

    public
    String getTotemPopString ( EntityPlayer player ) {
        return "\u00a7f" + ( this.getTotemPops ( player ) <= 0 ? "" : "-" + this.getTotemPops ( player ) + " " );
    }
}
