package dev.fxcte.creepyware.features.gui.components.items.buttons;

import dev.fxcte.creepyware.CreepyWare;
import dev.fxcte.creepyware.features.gui.CreepyWareGui;
import dev.fxcte.creepyware.features.modules.client.ClickGui;
import dev.fxcte.creepyware.features.modules.client.HUD;
import dev.fxcte.creepyware.features.setting.Bind;
import dev.fxcte.creepyware.features.setting.Setting;
import dev.fxcte.creepyware.util.ColorUtil;
import dev.fxcte.creepyware.util.MathUtil;
import dev.fxcte.creepyware.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public
class BindButton
        extends Button {
    private final Setting setting;
    public boolean isListening;

    public
    BindButton(Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.width = 15;
    }

    @Override
    public
    void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (ClickGui.getInstance().rainbowRolling.getValue()) {
            int color = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y, 0, this.renderer.scaledHeight)), CreepyWare.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue());
            int color1 = ColorUtil.changeAlpha(HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y + this.height, 0, this.renderer.scaledHeight)), CreepyWare.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue());
            RenderUtil.drawGradientRect(this.x, this.y, (float) this.width + 7.4f, (float) this.height - 0.5f, this.getState() ? (! this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y, 0, this.renderer.scaledHeight)) : color) : (! this.isHovering(mouseX, mouseY) ? 0x11555555 : - 2007673515), this.getState() ? (! this.isHovering(mouseX, mouseY) ? HUD.getInstance().colorMap.get(MathUtil.clamp((int) this.y + this.height, 0, this.renderer.scaledHeight)) : color1) : (! this.isHovering(mouseX, mouseY) ? 0x11555555 : - 2007673515));
        } else {
            RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f, this.getState() ? (! this.isHovering(mouseX, mouseY) ? CreepyWare.colorManager.getColorWithAlpha(((ClickGui) CreepyWare.moduleManager.getModuleByName("ClickGui")).hoverAlpha.getValue()) : CreepyWare.colorManager.getColorWithAlpha(((ClickGui) CreepyWare.moduleManager.getModuleByName("ClickGui")).alpha.getValue())) : (! this.isHovering(mouseX, mouseY) ? 0x11555555 : - 2007673515));
        }
        if (this.isListening) {
            CreepyWare.textManager.drawStringWithShadow("Listening...", this.x + 2.3f, this.y - 1.7f - (float) CreepyWareGui.getClickGui().getTextOffset(), this.getState() ? - 1 : - 5592406);
        } else {
            CreepyWare.textManager.drawStringWithShadow(this.setting.getName() + " " + "\u00a77" + this.setting.getValue().toString(), this.x + 2.3f, this.y - 1.7f - (float) CreepyWareGui.getClickGui().getTextOffset(), this.getState() ? - 1 : - 5592406);
        }
    }

    @Override
    public
    void update() {
        this.setHidden(! this.setting.isVisible());
    }

    @Override
    public
    void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_HARP, 1.0f));
        }
    }

    @Override
    public
    void onKeyTyped(char typedChar, int keyCode) {
        if (this.isListening) {
            Bind bind = new Bind(keyCode);
            if (bind.toString().equalsIgnoreCase("Escape")) {
                return;
            }
            if (bind.toString().equalsIgnoreCase("Delete")) {
                bind = new Bind(- 1);
            }
            this.setting.setValue(bind);
            super.onMouseClick();
        }
    }

    @Override
    public
    int getHeight() {
        return 14;
    }

    @Override
    public
    void toggle() {
        this.isListening = ! this.isListening;
    }

    @Override
    public
    boolean getState() {
        return ! this.isListening;
    }
}

