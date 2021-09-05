package dev.fxcte.creepyware.features.modules.render;

import dev.fxcte.creepyware.event.events.Render3DEvent;
import dev.fxcte.creepyware.features.modules.Module;
import dev.fxcte.creepyware.features.modules.client.Colors;
import dev.fxcte.creepyware.features.setting.Setting;
import dev.fxcte.creepyware.util.ColorUtil;
import dev.fxcte.creepyware.util.MathUtil;
import dev.fxcte.creepyware.util.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class StorageESP
        extends Module {
    private final Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(50.0f), Float.valueOf(1.0f), Float.valueOf(300.0f)));
    private final Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Speed", "Sync", 0.0, 0.0, false, 0));
    private final Setting<Boolean> chest = this.register(new Setting<Boolean>("Speed", "Chest", 0.0, 0.0, true, 0));
    private final Setting<Boolean> dispenser = this.register(new Setting<Boolean>("Speed", "Dispenser", 0.0, 0.0, false, 0));
    private final Setting<Boolean> shulker = this.register(new Setting<Boolean>("Speed", "Shulker", 0.0, 0.0, true, 0));
    private final Setting<Boolean> echest = this.register(new Setting<Boolean>("Speed", "Ender Chest", 0.0, 0.0, true, 0));
    private final Setting<Boolean> furnace = this.register(new Setting<Boolean>("Speed", "Furnace", 0.0, 0.0, false, 0));
    private final Setting<Boolean> hopper = this.register(new Setting<Boolean>("Speed", "Hopper", 0.0, 0.0, false, 0));
    private final Setting<Boolean> cart = this.register(new Setting<Boolean>("Speed", "Minecart", 0.0, 0.0, false, 0));
    private final Setting<Boolean> frame = this.register(new Setting<Boolean>("Speed", "Item Frame", 0.0, 0.0, false, 0));
    private final Setting<Boolean> box = this.register(new Setting<Boolean>("Speed", "Box", 0.0, 0.0, false, 0));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Object>("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.box.getValue()));
    private final Setting<Boolean> outline = this.register(new Setting<Boolean>("Speed", "Outline", 0.0, 0.0, true, 0));
    private final Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.outline.getValue()));

    public StorageESP() {
        super("StorageESP", "Highlights Containers.", Module.Category.RENDER, false, false, false);
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        int color;
        BlockPos pos;
        HashMap<BlockPos, Integer> positions = new HashMap<BlockPos, Integer>();
        for (TileEntity tileEntity : StorageESP.mc.world.loadedTileEntityList) {
            if (!(tileEntity instanceof TileEntityChest && this.chest.getValue() != false || tileEntity instanceof TileEntityDispenser && this.dispenser.getValue() != false || tileEntity instanceof TileEntityShulkerBox && this.shulker.getValue() != false || tileEntity instanceof TileEntityEnderChest && this.echest.getValue() != false || tileEntity instanceof TileEntityFurnace && this.furnace.getValue() != false) && (!(tileEntity instanceof TileEntityHopper) || !this.hopper.getValue().booleanValue()) || !(StorageESP.mc.player.getDistanceSq(pos = tileEntity.getPos()) <= MathUtil.square(this.range.getValue().floatValue())) || (color = this.getTileEntityColor(tileEntity)) == -1)
                continue;
            positions.put(pos, color);
        }
        for (Entity entity : StorageESP.mc.world.loadedEntityList) {
            if ((!(entity instanceof EntityItemFrame) || !this.frame.getValue().booleanValue()) && (!(entity instanceof EntityMinecartChest) || !this.cart.getValue().booleanValue()) || !(StorageESP.mc.player.getDistanceSq(pos = entity.getPosition()) <= MathUtil.square(this.range.getValue().floatValue())) || (color = this.getEntityColor(entity)) == -1)
                continue;
            positions.put(pos, color);
        }
        for (Map.Entry entry : positions.entrySet()) {
            BlockPos blockPos = (BlockPos) entry.getKey();
            color = (Integer) entry.getValue();
            RenderUtil.drawBoxESP(blockPos, this.colorSync.getValue() != false ? Colors.INSTANCE.getCurrentColor() : new Color(color), false, new Color(color), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
        }
    }

    private int getTileEntityColor(TileEntity tileEntity) {
        if (tileEntity instanceof TileEntityChest) {
            return ColorUtil.Colors.BLUE;
        }
        if (tileEntity instanceof TileEntityShulkerBox) {
            return ColorUtil.Colors.RED;
        }
        if (tileEntity instanceof TileEntityEnderChest) {
            return ColorUtil.Colors.PURPLE;
        }
        if (tileEntity instanceof TileEntityFurnace) {
            return ColorUtil.Colors.GRAY;
        }
        if (tileEntity instanceof TileEntityHopper) {
            return ColorUtil.Colors.DARK_RED;
        }
        if (tileEntity instanceof TileEntityDispenser) {
            return ColorUtil.Colors.ORANGE;
        }
        return -1;
    }

    private int getEntityColor(Entity entity) {
        if (entity instanceof EntityMinecartChest) {
            return ColorUtil.Colors.ORANGE;
        }
        if (entity instanceof EntityItemFrame && ((EntityItemFrame) entity).getDisplayedItem().getItem() instanceof ItemShulkerBox) {
            return ColorUtil.Colors.YELLOW;
        }
        if (entity instanceof EntityItemFrame && !(((EntityItemFrame) entity).getDisplayedItem().getItem() instanceof ItemShulkerBox)) {
            return ColorUtil.Colors.ORANGE;
        }
        return -1;
    }
}

