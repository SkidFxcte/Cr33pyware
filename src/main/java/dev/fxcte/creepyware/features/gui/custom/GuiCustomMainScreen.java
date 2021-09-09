package dev.fxcte.creepyware.features.gui.custom;

import dev.fxcte.creepyware.CreepyWare;
import dev.fxcte.creepyware.util.ParticleGenerator;
import dev.fxcte.creepyware.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;

public class GuiCustomMainScreen
        extends GuiScreen {
    private final ResourceLocation resourceLocation = new ResourceLocation("textures/background.png");
    private int y;
    private int x;
    private int singleplayerWidth;
    private int multiplayerWidth;
    private int settingsWidth;
    private int exitWidth;
    private int textHeight;
    private float xOffset;
    private float yOffset;

    public static Minecraft mc = Minecraft.getMinecraft();
    public static ParticleGenerator particleGenerator = new ParticleGenerator(100, mc.displayWidth, mc.displayHeight);
    public static void drawCompleteImage(float posX, float posY, float width, float height) {
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(width, height, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(width, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY < y + height;
    }

    public void initGui() {
        this.x = this.width / 4;
        this.y = this.height / 4 + 48;
        this.buttonList.add(new TextButton(0, this.x, this.y + 20, "Singleplayer"));
        this.buttonList.add(new TextButton(2, this.x, this.y + 44, "TheGang"));
        this.buttonList.add(new TextButton(2, this.x, this.y + 66, "Settings"));
        this.buttonList.add(new TextButton(2, this.x, this.y + 88, "Discord"));
        this.buttonList.add(new TextButton(2, this.x, this.y + 132, "EzLog"));
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void updateScreen() {
        super.updateScreen();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiCustomMainScreen.isHovered(this.x, this.y + 20, CreepyWare.textManager.getStringWidth("Singleplayer"), CreepyWare.textManager.getFontHeight(), mouseX, mouseY)) {
            mc.displayGuiScreen(new GuiWorldSelection(this));
        } else if (GuiCustomMainScreen.isHovered(this.x, this.y + 44, CreepyWare.textManager.getStringWidth("TheGang"), CreepyWare.textManager.getFontHeight(), mouseX, mouseY)) {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        } else if (GuiCustomMainScreen.isHovered(this.x, this.y + 66, CreepyWare.textManager.getStringWidth("settings"), CreepyWare.textManager.getFontHeight(), mouseX, mouseY)) {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        } else if (GuiCustomMainScreen.isHovered(this.x, this.y + 132, CreepyWare.textManager.getStringWidth("EzLog"), CreepyWare.textManager.getFontHeight(), mouseX, mouseY)) {
                mc.shutdown();
        } else if (GuiCustomMainScreen.isHovered(this.x, this.y + 88, CreepyWare.textManager.getStringWidth("discord"), CreepyWare.textManager.getFontHeight(), mouseX, mouseY)) {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI("https://discord.gg/gMZJd5UzYh"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.x = this.width / 4;
        this.y = this.height / 4 + 48;
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        mc.getTextureManager().bindTexture(this.resourceLocation);
        GuiCustomMainScreen.drawCompleteImage(-16.0f + this.xOffset, -9.0f + this.yOffset, this.width + 32, this.height + 18);
        particleGenerator.drawParticles(mouseX, mouseY);
        CreepyWare.textManager.drawStringBig("Cr33pyW4re", (float) this.x, (float) this.y - 20, Color.white.getRGB(), true);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public BufferedImage parseBackground(BufferedImage background) {
        int height;
        int width = 1920;
        int srcWidth = background.getWidth();
        int srcHeight = background.getHeight();
        for (height = 1080; width < srcWidth || height < srcHeight; width *= 2, height *= 2) {
        }
        BufferedImage imgNew = new BufferedImage(width, height, 2);
        Graphics g = imgNew.getGraphics();
        g.drawImage(background, 0, 0, null);
        g.dispose();
        return imgNew;
    }

    private static class TextButton
            extends GuiButton {
        public TextButton(int buttonId, int x, int y, String buttonText) {
            super(buttonId, x, y, CreepyWare.textManager.getStringWidth(buttonText), CreepyWare.textManager.getFontHeight(), buttonText);
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
                this.enabled = true;
                this.hovered = (float) mouseX >= (float) this.x - (float) CreepyWare.textManager.getStringWidth(this.displayString) / 2.0f && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
                CreepyWare.textManager.drawStringWithShadow(this.displayString, (float) this.x - (float) CreepyWare.textManager.getStringWidth(this.displayString) / 2.0f, this.y, Color.WHITE.getRGB());
                if (this.hovered) {
                    RenderUtil.drawLine((this.x - 5f) - (float) CreepyWare.textManager.getStringWidth(this.displayString) / 2.0f, this.y + 2 + CreepyWare.textManager.getFontHeight(), (float) this.x + (float) CreepyWare.textManager.getStringWidth(this.displayString) / 2.0f + 1.0f, this.y + 2 + CreepyWare.textManager.getFontHeight(), 1.0f, Color.GREEN.getRGB());
                    CreepyWare.textManager.drawStringSmall("Click me", (float) this.x, (float) this.y - 10, Color.white.getRGB(), false);
                }
            }
        }

        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            return this.enabled && this.visible && (float) mouseX >= (float) this.x - (float) CreepyWare.textManager.getStringWidth(this.displayString) / 2.0f && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        }
    }
}

