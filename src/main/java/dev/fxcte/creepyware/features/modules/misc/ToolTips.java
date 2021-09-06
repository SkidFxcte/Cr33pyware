package dev.fxcte.creepyware.features.modules.misc;

import dev.fxcte.creepyware.event.events.Render2DEvent;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.setting.Bind;
import dev.fxcte.creepyware.features.setting.Setting;
import dev.fxcte.creepyware.util.ColorUtil;
import dev.fxcte.creepyware.util.RenderUtil;
import dev.fxcte.creepyware.util.Timer;
import dev.fxcte.creepyware.util.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ToolTips
        extends Module {
    private static final ResourceLocation MAP = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation SHULKER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static ToolTips INSTANCE = new ToolTips();
    public Setting<Boolean> maps = this.register(new Setting<Boolean>("Speed", "Maps", 0.0, 0.0, true, 0));
    public Setting<Boolean> shulkers = this.register(new Setting<Boolean>("Speed", "ShulkerViewer", 0.0, 0.0, true, 0));
    public Setting<Bind> peek = this.register(new Setting<Bind>("Speed", "Peek", 0.0, 0.0, new Bind(-1), 0));
    public Setting<Boolean> shulkerSpy = this.register(new Setting<Boolean>("Speed", "ShulkerSpy", 0.0, 0.0, true, 0));
    public Setting<Boolean> render = this.register(new Setting<Object>("Render", Boolean.valueOf(true), v -> this.shulkerSpy.getValue()));
    public Setting<Boolean> own = this.register(new Setting<Object>("OwnShulker", Boolean.valueOf(true), v -> this.shulkerSpy.getValue()));
    public Setting<Integer> cooldown = this.register(new Setting<Object>("ShowForS", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(5), v -> this.shulkerSpy.getValue()));
    public Setting<Boolean> textColor = this.register(new Setting<Object>("TextColor", Boolean.valueOf(false), v -> this.shulkers.getValue()));
    private final Setting<Integer> red = this.register(new Setting<Object>("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.textColor.getValue()));
    private final Setting<Integer> green = this.register(new Setting<Object>("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.textColor.getValue()));
    private final Setting<Integer> blue = this.register(new Setting<Object>("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.textColor.getValue()));
    private final Setting<Integer> alpha = this.register(new Setting<Object>("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.textColor.getValue()));
    public Setting<Boolean> offsets = this.register(new Setting<Boolean>("Speed", "Offsets", 0.0, 0.0, false, 0));
    private final Setting<Integer> yPerPlayer = this.register(new Setting<Object>("Y/Player", Integer.valueOf(18), v -> this.offsets.getValue()));
    private final Setting<Integer> xOffset = this.register(new Setting<Object>("XOffset", Integer.valueOf(4), v -> this.offsets.getValue()));
    private final Setting<Integer> yOffset = this.register(new Setting<Object>("YOffset", Integer.valueOf(2), v -> this.offsets.getValue()));
    private final Setting<Integer> trOffset = this.register(new Setting<Object>("TROffset", Integer.valueOf(2), v -> this.offsets.getValue()));
    public Setting<Integer> invH = this.register(new Setting<Object>("InvH", Integer.valueOf(3), v -> this.offsets.getValue()));
    public Map<EntityPlayer, ItemStack> spiedPlayers = new ConcurrentHashMap<EntityPlayer, ItemStack>();
    public Map<EntityPlayer, Timer> playerTimers = new ConcurrentHashMap<EntityPlayer, Timer>();
    private int textRadarY = 0;

    public ToolTips() {
        super("ToolTips", "Several tweaks for tooltips.", Module.Category.MISC, true, false, false);
        this.setInstance();
    }

    public static ToolTips getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ToolTips();
        }
        return INSTANCE;
    }

    public static void displayInv(ItemStack stack, String name) {
        try {
            Item item = stack.getItem();
            TileEntityShulkerBox entityBox = new TileEntityShulkerBox();
            ItemShulkerBox shulker = (ItemShulkerBox) item;
            entityBox.blockType = shulker.getBlock();
            entityBox.setWorld(ToolTips.mc.world);
            ItemStackHelper.loadAllItems(stack.getTagCompound().getCompoundTag("BlockEntityTag"), entityBox.items);
            entityBox.readFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
            entityBox.setCustomName(name == null ? stack.getDisplayName() : name);
            new Thread(() -> {
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                ToolTips.mc.player.displayGUIChest(entityBox);
            }).start();
        } catch (Exception exception) {
            // empty catch block
        }
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        ItemStack stack;
        Slot slot;
        if (ToolTips.fullNullCheck() || !this.shulkerSpy.getValue().booleanValue()) {
            return;
        }
        if (this.peek.getValue().getKey() != -1 && ToolTips.mc.currentScreen instanceof GuiContainer && Keyboard.isKeyDown(this.peek.getValue().getKey()) && (slot = ((GuiContainer) ToolTips.mc.currentScreen).getSlotUnderMouse()) != null && (stack = slot.getStack()) != null && stack.getItem() instanceof ItemShulkerBox) {
            ToolTips.displayInv(stack, null);
        }
        for (EntityPlayer player : ToolTips.mc.world.playerEntities) {
            if (player == null || player.getHeldItemMainhand() == null || !(player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox) || !this.own.getValue().booleanValue() && ToolTips.mc.player.equals(player))
                continue;
            ItemStack stack2 = player.getHeldItemMainhand();
            this.spiedPlayers.put(player, stack2);
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        if (ToolTips.fullNullCheck() || !this.shulkerSpy.getValue().booleanValue() || !this.render.getValue().booleanValue()) {
            return;
        }
        int x = -4 + this.xOffset.getValue();
        int y = 10 + this.yOffset.getValue();
        this.textRadarY = 0;
        for (EntityPlayer player : ToolTips.mc.world.playerEntities) {
            Timer playerTimer;
            if (this.spiedPlayers.get(player) == null) continue;
            if (player.getHeldItemMainhand() == null || !(player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox)) {
                playerTimer = this.playerTimers.get(player);
                if (playerTimer == null) {
                    Timer timer = new Timer();
                    timer.reset();
                    this.playerTimers.put(player, timer);
                } else if (playerTimer.passedS(this.cooldown.getValue().intValue())) {
                    continue;
                }
            } else if (player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox && (playerTimer = this.playerTimers.get(player)) != null) {
                playerTimer.reset();
                this.playerTimers.put(player, playerTimer);
            }
            ItemStack stack = this.spiedPlayers.get(player);
            this.renderShulkerToolTip(stack, x, y, player.getName());
            this.textRadarY = (y += this.yPerPlayer.getValue() + 60) - 10 - this.yOffset.getValue() + this.trOffset.getValue();
        }
    }

    public int getTextRadarY() {
        return this.textRadarY;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void makeTooltip(ItemTooltipEvent event) {
    }

    @SubscribeEvent
    public void renderTooltip(RenderTooltipEvent.PostText event) {
        MapData mapData;
        if (this.maps.getValue().booleanValue() && !event.getStack().isEmpty() && event.getStack().getItem() instanceof ItemMap && (mapData = Items.FILLED_MAP.getMapData(event.getStack(), ToolTips.mc.world)) != null) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f);
            RenderHelper.disableStandardItemLighting();
            Util.mc.getTextureManager().bindTexture(MAP);
            Tessellator instance = Tessellator.getInstance();
            BufferBuilder buffer = instance.getBuffer();
            int n = 7;
            float n2 = 135.0f;
            float n3 = 0.5f;
            GlStateManager.translate((float) event.getX(), (float) event.getY() - n2 * n3 - 5.0f, 0.0f);
            GlStateManager.scale(n3, n3, n3);
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(-n, n2, 0.0).tex(0.0, 1.0).endVertex();
            buffer.pos(n2, n2, 0.0).tex(1.0, 1.0).endVertex();
            buffer.pos(n2, -n, 0.0).tex(1.0, 0.0).endVertex();
            buffer.pos(-n, -n, 0.0).tex(0.0, 0.0).endVertex();
            instance.draw();
            ToolTips.mc.entityRenderer.getMapItemRenderer().renderMap(mapData, false);
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    public void renderShulkerToolTip(ItemStack stack, int x, int y, String name) {
        NBTTagCompound blockEntityTag;
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10) && (blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag")).hasKey("Items", 9)) {
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            Util.mc.getTextureManager().bindTexture(SHULKER_GUI_TEXTURE);
            RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
            RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54 + this.invH.getValue(), 500);
            RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
            GlStateManager.disableDepth();
            Color color = new Color(0, 0, 0, 255);
            if (this.textColor.getValue().booleanValue()) {
                color = new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
            }
            this.renderer.drawStringWithShadow(name == null ? stack.getDisplayName() : name, x + 8, y + 6, ColorUtil.toRGBA(color));
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            NonNullList nonnulllist = NonNullList.withSize(27, (Object) ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(blockEntityTag, nonnulllist);
            for (int i = 0; i < nonnulllist.size(); ++i) {
                int iX = x + i % 9 * 18 + 8;
                int iY = y + i / 9 * 18 + 18;
                ItemStack itemStack = (ItemStack) nonnulllist.get(i);
                ToolTips.mc.getRenderItem().zLevel = 501.0f;
                RenderUtil.itemRender.renderItemAndEffectIntoGUI(itemStack, iX, iY);
                RenderUtil.itemRender.renderItemOverlayIntoGUI(ToolTips.mc.fontRenderer, itemStack, iX, iY, null);
                ToolTips.mc.getRenderItem().zLevel = 0.0f;
            }
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}

