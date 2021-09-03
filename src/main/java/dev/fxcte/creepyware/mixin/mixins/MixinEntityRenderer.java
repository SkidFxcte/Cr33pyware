package dev.fxcte.creepyware.mixin.mixins;


import dev.fxcte.creepyware.MinecraftInstance;
import dev.fxcte.creepyware.event.events.PerspectiveEvent;
import dev.fxcte.creepyware.features.modules.client.Notifications;
import dev.fxcte.creepyware.features.modules.render.CameraClip;
import dev.fxcte.creepyware.features.modules.render.NoRender;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityRenderer.class})
public abstract class MixinEntityRenderer {
    private boolean injection = true;
    @Shadow
    public ItemStack itemActivationItem;
    @Shadow
    @Final
    public Minecraft mc;

    @Shadow
    public abstract void getMouseOver(float var1);

    @Inject(method={"renderItemActivation"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderItemActivationHook(CallbackInfo info) {
        if (this.itemActivationItem != null && NoRender.getInstance().isOn() && NoRender.getInstance().totemPops.getValue().booleanValue() && this.itemActivationItem.getItem() == Items.TOTEM_OF_UNDYING) {
            info.cancel();
        }
    }
    @Redirect(method={"setupCameraTransform"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float) MinecraftInstance.mc.displayWidth / (float)MinecraftInstance.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)event);
        Project.gluPerspective((float)fovy, (float)event.getAspect(), (float)zNear, (float)zFar);
    }

    @Redirect(method={"renderWorldPass"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float)MinecraftInstance.mc.displayWidth / (float)MinecraftInstance.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)event);
        Project.gluPerspective((float)fovy, (float)event.getAspect(), (float)zNear, (float)zFar);
    }

    @Redirect(method={"renderCloudsCheck"}, at=@At(value="INVOKE", target="Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(float fovy, float aspect, float zNear, float zFar) {
        PerspectiveEvent event = new PerspectiveEvent((float)MinecraftInstance.mc.displayWidth / (float)MinecraftInstance.mc.displayHeight);
        MinecraftForge.EVENT_BUS.post((Event)event);
        Project.gluPerspective((float)fovy, (float)event.getAspect(), (float)zNear, (float)zFar);
    }

    @Inject(method={"updateLightmap"}, at={@At(value="HEAD")}, cancellable=true)
    private void updateLightmap(float partialTicks, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && (NoRender.getInstance().skylight.getValue() == NoRender.Skylight.ENTITY || NoRender.getInstance().skylight.getValue() == NoRender.Skylight.ALL)) {
            info.cancel();
        }
    }

    @Inject(method={"getMouseOver(F)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void getMouseOverHook(float partialTicks, CallbackInfo info) {
        if (this.injection) {
            block3: {
                info.cancel();
                this.injection = false;
                try {
                    this.getMouseOver(partialTicks);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    if (!Notifications.getInstance().isOn() || !Notifications.getInstance().crash.getValue().booleanValue()) break block3;
                    Notifications.displayCrash(e);
                }
            }
            this.injection = true;
        }
    }

    @Redirect(method={"setupCameraTransform"}, at=@At(value="FIELD", target="Lnet/minecraft/client/entity/EntityPlayerSP;prevTimeInPortal:F"))
    public float prevTimeInPortalHook(EntityPlayerSP entityPlayerSP) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().nausea.getValue().booleanValue()) {
            return -3.4028235E38f;
        }
        return entityPlayerSP.prevTimeInPortal;
    }

    @Inject(method={"setupFog"}, at={@At(value="HEAD")}, cancellable=true)
    public void setupFogHook(int startCoords, float partialTicks, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fog.getValue() == NoRender.Fog.NOFOG) {
            info.cancel();
        }
    }

    @Redirect(method={"setupFog"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ActiveRenderInfo;getBlockStateAtEntityViewpoint(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/block/state/IBlockState;"))
    public IBlockState getBlockStateAtEntityViewpointHook(World worldIn, Entity entityIn, float p_186703_2_) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fog.getValue() == NoRender.Fog.AIR) {
            return Blocks.AIR.defaultBlockState;
        }
        return ActiveRenderInfo.getBlockStateAtEntityViewpoint((World)worldIn, (Entity)entityIn, (float)p_186703_2_);
    }

    @Inject(method={"hurtCameraEffect"}, at={@At(value="HEAD")}, cancellable=true)
    public void hurtCameraEffectHook(float ticks, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().hurtcam.getValue().booleanValue()) {
            info.cancel();
        }
    }
    @ModifyVariable(method={"orientCamera"}, ordinal=3, at=@At(value="STORE", ordinal=0), require=1)
    public double changeCameraDistanceHook(double range) {
        return CameraClip.getInstance().isEnabled() && CameraClip.getInstance().extend.getValue() != false ? CameraClip.getInstance().distance.getValue() : range;
    }

    @ModifyVariable(method={"orientCamera"}, ordinal=7, at=@At(value="STORE", ordinal=0), require=1)
    public double orientCameraHook(double range) {
        return CameraClip.getInstance().isEnabled() && CameraClip.getInstance().extend.getValue() != false ? CameraClip.getInstance().distance.getValue() : (CameraClip.getInstance().isEnabled() && CameraClip.getInstance().extend.getValue() == false ? 4.0 : range);
    }
}

