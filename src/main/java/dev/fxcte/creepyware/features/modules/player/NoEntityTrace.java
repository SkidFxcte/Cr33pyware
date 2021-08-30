package dev.fxcte.creepyware.features.modules.player;

import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Setting;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;

public
class NoEntityTrace
        extends Module {
    private static NoEntityTrace INSTANCE = new NoEntityTrace ( );
    public Setting < Boolean > pick = this.register ( new Setting <> ( "Pick" , true ) );
    public Setting < Boolean > gap = this.register ( new Setting <> ( "Gap" , false ) );
    public Setting < Boolean > obby = this.register ( new Setting <> ( "Obby" , false ) );
    public boolean noTrace;

    public
    NoEntityTrace ( ) {
        super ( "NoEntityTrace" , "Mine through entities" , Module.Category.PLAYER , false , false , false );
        this.setInstance ( );
    }

    public static
    NoEntityTrace getINSTANCE ( ) {
        if ( INSTANCE == null ) {
            INSTANCE = new NoEntityTrace ( );
        }
        return INSTANCE;
    }

    private
    void setInstance ( ) {
        INSTANCE = this;
    }

    @Override
    public
    void onUpdate ( ) {
        Item item = NoEntityTrace.mc.player.getHeldItemMainhand ( ).getItem ( );
        if ( item instanceof ItemPickaxe && this.pick.getValue ( ) ) {
            this.noTrace = true;
            return;
        }
        if ( item == Items.GOLDEN_APPLE && this.gap.getValue ( ) ) {
            this.noTrace = true;
            return;
        }
        if ( item instanceof ItemBlock ) {
            this.noTrace = ( (ItemBlock) item ).getBlock ( ) == Blocks.OBSIDIAN && this.obby.getValue ( );
            return;
        }
        this.noTrace = false;
    }
}
