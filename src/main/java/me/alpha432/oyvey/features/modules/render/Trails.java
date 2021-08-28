package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;

public
class Trails extends Module {
    private final Setting lineWidth = this.register ( new Setting <> ( "LineWidth" , 1.5F , 0.1F , 5.0F ) );
    private final Setting red = this.register ( new Setting <> ( "Red" , 0 , 0 , 255 ) );
    private final Setting green = this.register ( new Setting <> ( "Green" , 255 , 0 , 255 ) );
    private final Setting blue = this.register ( new Setting <> ( "Blue" , 0 , 0 , 255 ) );
    private final Setting alpha = this.register ( new Setting <> ( "Alpha" , 255 , 0 , 255 ) );
    private final Map < Entity, ArrayList < ? > > renderMap = new HashMap <> ( );

    public
    Trails ( ) {
        super ( "Trails" , "Draws trails on projectiles" , Category.RENDER , true , false , false );
    }

    @SubscribeEvent
    public
    void onUpdateWalkingPlayer ( UpdateWalkingPlayerEvent event ) {
        Iterator < Entity > var2 = mc.world.loadedEntityList.iterator ( );

        while ( true ) {
            Entity entity;
            do {
                if ( ! var2.hasNext ( ) ) {
                    return;
                }

                entity = var2.next ( );
            } while ( ! ( entity instanceof EntityThrowable ) && ! ( entity instanceof EntityArrow ) );
            if ( entity instanceof EntityExpBottle ) return;
            if ( entity instanceof EntityArrow ) return;

            ArrayList < ? > vectors;
            if ( this.renderMap.get ( entity ) != null ) {
                vectors = this.renderMap.get ( entity );
            } else {
                vectors = new ArrayList <> ( );
            }
            Vec3d interp;
            interp = EntityUtil.getInterpolatedRenderPos ( entity , mc.getRenderPartialTicks ( ) );
            ( (List) vectors ).add ( new Vec3d ( entity.getEntityBoundingBox ( ).minX - 0.05 - entity.posX + interp.x , entity.getEntityBoundingBox ( ).minY - 0.0 - entity.posY + interp.y , entity.getEntityBoundingBox ( ).minZ - 0.05 - entity.posZ + interp.z ) );
            this.renderMap.put ( entity , vectors );
        }
    }

    public
    void onRender3D ( Render3DEvent event ) {
        Iterator < Entity > var2 = mc.world.loadedEntityList.iterator ( );

        while ( true ) {
            Entity entity;
            do {
                if ( ! var2.hasNext ( ) ) {
                    return;
                }

                entity = var2.next ( );
            } while ( ! this.renderMap.containsKey ( entity ) );

            GlStateManager.pushMatrix ( );
            RenderUtil.GLPre ( (Float) this.lineWidth.getValue ( ) );
            GlStateManager.enableBlend ( );
            GlStateManager.disableTexture2D ( );
            GlStateManager.depthMask ( false );
            GlStateManager.disableDepth ( );
            GlStateManager.tryBlendFuncSeparate ( SourceFactor.SRC_ALPHA , DestFactor.ONE_MINUS_SRC_ALPHA , SourceFactor.ONE , DestFactor.ZERO );
            GL11.glColor4f ( (float) (Integer) this.red.getValue ( ) / 255.0F , (float) (Integer) this.green.getValue ( ) / 255.0F , (float) (Integer) this.blue.getValue ( ) / 255.0F , (float) (Integer) this.alpha.getValue ( ) / 255.0F );
            GL11.glLineWidth ( (Float) this.lineWidth.getValue ( ) );
            GL11.glBegin ( 1 );

            for (int i = 0; i < ( this.renderMap.get ( entity ) ).size ( ) - 1; ++ i) {
                GL11.glVertex3d ( ( (Vec3d) ( this.renderMap.get ( entity ) ).get ( i ) ).x , ( (Vec3d) ( this.renderMap.get ( entity ) ).get ( i ) ).y , ( (Vec3d) ( this.renderMap.get ( entity ) ).get ( i ) ).z );
                GL11.glVertex3d ( ( (Vec3d) ( this.renderMap.get ( entity ) ).get ( i + 1 ) ).x , ( (Vec3d) ( this.renderMap.get ( entity ) ).get ( i + 1 ) ).y , ( (Vec3d) ( this.renderMap.get ( entity ) ).get ( i + 1 ) ).z );
            }

            GL11.glEnd ( );
            GlStateManager.resetColor ( );
            GlStateManager.enableDepth ( );
            GlStateManager.depthMask ( true );
            GlStateManager.enableTexture2D ( );
            GlStateManager.disableBlend ( );
            RenderUtil.GlPost ( );
            GlStateManager.popMatrix ( );
        }
    }
}
