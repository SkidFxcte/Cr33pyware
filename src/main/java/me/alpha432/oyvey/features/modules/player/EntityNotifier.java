package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.init.SoundEvents;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public
class EntityNotifier
        extends Module {
    private final Set < Entity > ghasts = new HashSet <> ( );
    private final Set < Entity > donkeys = new HashSet <> ( );
    private final Set < Entity > mules = new HashSet <> ( );
    private final Set < Entity > llamas = new HashSet <> ( );
    private final Image image = Toolkit.getDefaultToolkit ( ).getImage ( "resources/phobos.png" );
    private final TrayIcon icon = new TrayIcon ( this.image , "Perry Phobos" );
    public Setting < Boolean > Chat = this.register ( new Setting <> ( "Chat" , true ) );
    public Setting < Boolean > Sound = this.register ( new Setting <> ( "Sound" , true ) );
    public Setting < Boolean > Desktop = this.register ( new Setting <> ( "DesktopNotifs" , true ) );
    public Setting < Boolean > Ghasts = this.register ( new Setting <> ( "Ghasts" , true ) );
    public Setting < Boolean > Donkeys = this.register ( new Setting <> ( "Donkeys" , true ) );
    public Setting < Boolean > Mules = this.register ( new Setting <> ( "Mules" , true ) );
    public Setting < Boolean > Llamas = this.register ( new Setting <> ( "Llamas" , true ) );

    public
    EntityNotifier ( ) {
        super ( "EntityNotifier" , "Helps you find certain things" , Category.PLAYER , true , false , false );
        this.icon.setImageAutoSize ( true );
        try {
            SystemTray tray = SystemTray.getSystemTray ( );
            tray.add ( this.icon );
        } catch ( Exception exception ) {
            exception.printStackTrace ( );
        }
    }

    @Override
    public
    void onEnable ( ) {
        this.ghasts.clear ( );
        this.donkeys.clear ( );
        this.mules.clear ( );
        this.llamas.clear ( );
    }

    @Override
    public
    void onUpdate ( ) {
        if ( this.Ghasts.getValue ( ) ) {
            for (Entity entity : EntityNotifier.mc.world.getLoadedEntityList ( )) {
                if ( ! ( entity instanceof EntityGhast ) || this.ghasts.contains ( entity ) ) continue;
                if ( this.Chat.getValue ( ) ) {
                    Command.sendMessage ( "Ghast Detected at: " + Math.round ( entity.getPosition ( ).getX ( ) ) + "x, " + Math.round ( entity.getPosition ( ).getY ( ) ) + "y, " + Math.round ( entity.getPosition ( ).getZ ( ) ) + "z." );
                }
                this.ghasts.add ( entity );
                if ( ! this.Desktop.getValue ( ) ) continue;
                this.icon.displayMessage ( "Creepyware" , "I found a ghast at: " + Math.round ( entity.getPosition ( ).getX ( ) ) + "x, " + Math.round ( entity.getPosition ( ).getY ( ) ) + "y, " + Math.round ( entity.getPosition ( ).getZ ( ) ) + "z." , TrayIcon.MessageType.INFO );
                if ( ! this.Sound.getValue ( ) ) continue;
                EntityNotifier.mc.player.playSound ( SoundEvents.BLOCK_ANVIL_DESTROY , 1.0f , 1.0f );
            }
        }
        if ( this.Donkeys.getValue ( ) ) {
            for (Entity entity : EntityNotifier.mc.world.getLoadedEntityList ( )) {
                if ( ! ( entity instanceof EntityDonkey ) || this.donkeys.contains ( entity ) ) continue;
                if ( this.Chat.getValue ( ) ) {
                    Command.sendMessage ( "Donkey Detected at: " + Math.round ( entity.getPosition ( ).getX ( ) ) + "x, " + Math.round ( entity.getPosition ( ).getY ( ) ) + "y, " + Math.round ( entity.getPosition ( ).getZ ( ) ) + "z." );
                }
                this.donkeys.add ( entity );
                if ( ! this.Desktop.getValue ( ) ) continue;
                this.icon.displayMessage ( "Creepyware" , "I found a donkey at: " + Math.round ( entity.getPosition ( ).getX ( ) ) + "x, " + Math.round ( entity.getPosition ( ).getY ( ) ) + "y, " + Math.round ( entity.getPosition ( ).getZ ( ) ) + "z." , TrayIcon.MessageType.INFO );
                if ( ! this.Sound.getValue ( ) ) continue;
                EntityNotifier.mc.player.playSound ( SoundEvents.BLOCK_ANVIL_DESTROY , 1.0f , 1.0f );
            }
        }
        if ( this.Mules.getValue ( ) ) {
            for (Entity entity : EntityNotifier.mc.world.getLoadedEntityList ( )) {
                if ( ! ( entity instanceof EntityMule ) || this.mules.contains ( entity ) ) continue;
                if ( this.Chat.getValue ( ) ) {
                    Command.sendMessage ( "Mule Detected at: " + Math.round ( entity.getPosition ( ).getX ( ) ) + "x, " + Math.round ( entity.getPosition ( ).getY ( ) ) + "y, " + Math.round ( entity.getPosition ( ).getZ ( ) ) + "z." );
                }
                this.mules.add ( entity );
                if ( ! this.Desktop.getValue ( ) ) continue;
                this.icon.displayMessage ( "Creepyware" , "I found a mule at: " + Math.round ( entity.getPosition ( ).getX ( ) ) + "x, " + Math.round ( entity.getPosition ( ).getY ( ) ) + "y, " + Math.round ( entity.getPosition ( ).getZ ( ) ) + "z." , TrayIcon.MessageType.INFO );
                if ( ! this.Sound.getValue ( ) ) continue;
                EntityNotifier.mc.player.playSound ( SoundEvents.BLOCK_ANVIL_DESTROY , 1.0f , 1.0f );
            }
        }
        if ( this.Llamas.getValue ( ) ) {
            for (Entity entity : EntityNotifier.mc.world.getLoadedEntityList ( )) {
                if ( ! ( entity instanceof EntityLlama ) || this.llamas.contains ( entity ) ) continue;
                if ( this.Chat.getValue ( ) ) {
                    Command.sendMessage ( "Llama Detected at: " + Math.round ( entity.getPosition ( ).getX ( ) ) + "x, " + Math.round ( entity.getPosition ( ).getY ( ) ) + "y, " + Math.round ( entity.getPosition ( ).getZ ( ) ) + "z." );
                }
                this.llamas.add ( entity );
                if ( ! this.Desktop.getValue ( ) ) continue;
                this.icon.displayMessage ( "Creepyware" , "I found a llama at: " + Math.round ( entity.getPosition ( ).getX ( ) ) + "x, " + Math.round ( entity.getPosition ( ).getY ( ) ) + "y, " + Math.round ( entity.getPosition ( ).getZ ( ) ) + "z." , TrayIcon.MessageType.INFO );
                if ( ! this.Sound.getValue ( ) ) continue;
                EntityNotifier.mc.player.playSound ( SoundEvents.BLOCK_ANVIL_DESTROY , 1.0f , 1.0f );
            }
        }
    }
}