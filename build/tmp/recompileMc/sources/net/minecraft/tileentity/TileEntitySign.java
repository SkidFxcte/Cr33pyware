package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntitySign extends TileEntity
{
    public final ITextComponent[] signText = new ITextComponent[] {new TextComponentString(""), new TextComponentString(""), new TextComponentString(""), new TextComponentString("")};
    /**
     * The index of the line currently being edited. Only used on client side, but defined on both. Note this is only
     * really used when the > < are going to be visible.
     */
    public int lineBeingEdited = -1;
    private boolean isEditable = true;
    private EntityPlayer player;
    private final CommandResultStats stats = new CommandResultStats();

    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        for (int i = 0; i < 4; ++i)
        {
            String s = ITextComponent.Serializer.componentToJson(this.signText[i]);
            compound.setString("Text" + (i + 1), s);
        }

        this.stats.writeStatsToNBT(compound);
        return compound;
    }

    protected void setWorldCreate(World worldIn)
    {
        this.setWorld(worldIn);
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        this.isEditable = false;
        super.readFromNBT(compound);
        ICommandSender icommandsender = new ICommandSender()
        {
            /**
             * Gets the name of this thing. This method has slightly different behavior depending on the interface (for
             * <a href="https://github.com/ModCoderPack/MCPBot-Issues/issues/14">technical reasons</a> the same method
             * is used for both IWorldNameable and ICommandSender):
             *  
             * <dl>
             * <dt>{@link net.minecraft.util.INameable#getName() INameable.getName()}</dt>
             * <dd>Returns the name of this inventory. If this {@linkplain net.minecraft.inventory#hasCustomName() has a
             * custom name} then this <em>should</em> be a direct string; otherwise it <em>should</em> be a valid
             * translation string.</dd>
             * <dd>However, note that <strong>the translation string may be invalid</strong>, as is the case for {@link
             * net.minecraft.tileentity.TileEntityBanner TileEntityBanner} (always returns nonexistent translation code
             * <code>banner</code> without a custom name), {@link net.minecraft.block.BlockAnvil.Anvil BlockAnvil$Anvil}
             * (always returns <code>anvil</code>), {@link net.minecraft.block.BlockWorkbench.InterfaceCraftingTable
             * BlockWorkbench$InterfaceCraftingTable} (always returns <code>crafting_table</code>), {@link
             * net.minecraft.inventory.InventoryCraftResult InventoryCraftResult} (always returns <code>Result</code>)
             * and the {@link net.minecraft.entity.item.EntityMinecart EntityMinecart} family (uses the entity
             * definition). This is not an exaustive list.</dd>
             * <dd>In general, this method should be safe to use on tile entities that implement IInventory.</dd>
             * <dt>{@link net.minecraft.command.ICommandSender#getName() ICommandSender.getName()} and {@link
             * net.minecraft.entity.Entity#getName() Entity.getName()}</dt>
             * <dd>Returns a valid, displayable name (which may be localized). For most entities, this is the translated
             * version of its translation string (obtained via {@link net.minecraft.entity.EntityList#getEntityString
             * EntityList.getEntityString}).</dd>
             * <dd>If this entity has a custom name set, this will return that name.</dd>
             * <dd>For some entities, this will attempt to translate a nonexistent translation string; see <a
             * href="https://bugs.mojang.com/browse/MC-68446">MC-68446</a>. For {@linkplain
             * net.minecraft.entity.player.EntityPlayer#getName() players} this returns the player's name. For
             * {@linkplain net.minecraft.entity.passive.EntityOcelot ocelots} this may return the translation of
             * <code>entity.Cat.name</code> if it is tamed. For {@linkplain
             * net.minecraft.entity.item.EntityItem#getName() item entities}, this will attempt to return the name of
             * the item in that item entity. In all cases other than players, the custom name will overrule this.</dd>
             * <dd>For non-entity command senders, this will return some arbitrary name, such as "Rcon" or
             * "Server".</dd>
             * </dl>
             */
            public String getName()
            {
                return "Sign";
            }
            /**
             * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
             */
            public boolean canUseCommand(int permLevel, String commandName)
            {
                return permLevel <= 2; //Forge: Fixes  MC-75630 - Exploit with signs and command blocks
            }
            /**
             * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
             * return the coordinates 0, 0, 0
             */
            public BlockPos getPosition()
            {
                return TileEntitySign.this.pos;
            }
            /**
             * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
             * return 0.0D, 0.0D, 0.0D
             */
            public Vec3d getPositionVector()
            {
                return new Vec3d((double)TileEntitySign.this.pos.getX() + 0.5D, (double)TileEntitySign.this.pos.getY() + 0.5D, (double)TileEntitySign.this.pos.getZ() + 0.5D);
            }
            /**
             * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
             * return the overworld
             */
            public World getEntityWorld()
            {
                return TileEntitySign.this.world;
            }
            /**
             * Get the Minecraft server instance
             */
            public MinecraftServer getServer()
            {
                return TileEntitySign.this.world.getMinecraftServer();
            }
        };

        for (int i = 0; i < 4; ++i)
        {
            String s = compound.getString("Text" + (i + 1));
            ITextComponent itextcomponent = ITextComponent.Serializer.jsonToComponent(s);

            try
            {
                this.signText[i] = TextComponentUtils.processComponent(icommandsender, itextcomponent, (Entity)null);
            }
            catch (CommandException var7)
            {
                this.signText[i] = itextcomponent;
            }
        }

        this.stats.readStatsFromNBT(compound);
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(this.pos, 9, this.getUpdateTag());
    }

    /**
     * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
     * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
     */
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }

    public boolean onlyOpsCanSetNbt()
    {
        return true;
    }

    public boolean getIsEditable()
    {
        return this.isEditable;
    }

    /**
     * Sets the sign's isEditable flag to the specified parameter.
     */
    @SideOnly(Side.CLIENT)
    public void setEditable(boolean isEditableIn)
    {
        this.isEditable = isEditableIn;

        if (!isEditableIn)
        {
            this.player = null;
        }
    }

    public void setPlayer(EntityPlayer playerIn)
    {
        this.player = playerIn;
    }

    public EntityPlayer getPlayer()
    {
        return this.player;
    }

    public boolean executeCommand(final EntityPlayer playerIn)
    {
        ICommandSender icommandsender = new ICommandSender()
        {
            /**
             * Gets the name of this thing. This method has slightly different behavior depending on the interface (for
             * <a href="https://github.com/ModCoderPack/MCPBot-Issues/issues/14">technical reasons</a> the same method
             * is used for both IWorldNameable and ICommandSender):
             *  
             * <dl>
             * <dt>{@link net.minecraft.util.INameable#getName() INameable.getName()}</dt>
             * <dd>Returns the name of this inventory. If this {@linkplain net.minecraft.inventory#hasCustomName() has a
             * custom name} then this <em>should</em> be a direct string; otherwise it <em>should</em> be a valid
             * translation string.</dd>
             * <dd>However, note that <strong>the translation string may be invalid</strong>, as is the case for {@link
             * net.minecraft.tileentity.TileEntityBanner TileEntityBanner} (always returns nonexistent translation code
             * <code>banner</code> without a custom name), {@link net.minecraft.block.BlockAnvil.Anvil BlockAnvil$Anvil}
             * (always returns <code>anvil</code>), {@link net.minecraft.block.BlockWorkbench.InterfaceCraftingTable
             * BlockWorkbench$InterfaceCraftingTable} (always returns <code>crafting_table</code>), {@link
             * net.minecraft.inventory.InventoryCraftResult InventoryCraftResult} (always returns <code>Result</code>)
             * and the {@link net.minecraft.entity.item.EntityMinecart EntityMinecart} family (uses the entity
             * definition). This is not an exaustive list.</dd>
             * <dd>In general, this method should be safe to use on tile entities that implement IInventory.</dd>
             * <dt>{@link net.minecraft.command.ICommandSender#getName() ICommandSender.getName()} and {@link
             * net.minecraft.entity.Entity#getName() Entity.getName()}</dt>
             * <dd>Returns a valid, displayable name (which may be localized). For most entities, this is the translated
             * version of its translation string (obtained via {@link net.minecraft.entity.EntityList#getEntityString
             * EntityList.getEntityString}).</dd>
             * <dd>If this entity has a custom name set, this will return that name.</dd>
             * <dd>For some entities, this will attempt to translate a nonexistent translation string; see <a
             * href="https://bugs.mojang.com/browse/MC-68446">MC-68446</a>. For {@linkplain
             * net.minecraft.entity.player.EntityPlayer#getName() players} this returns the player's name. For
             * {@linkplain net.minecraft.entity.passive.EntityOcelot ocelots} this may return the translation of
             * <code>entity.Cat.name</code> if it is tamed. For {@linkplain
             * net.minecraft.entity.item.EntityItem#getName() item entities}, this will attempt to return the name of
             * the item in that item entity. In all cases other than players, the custom name will overrule this.</dd>
             * <dd>For non-entity command senders, this will return some arbitrary name, such as "Rcon" or
             * "Server".</dd>
             * </dl>
             */
            public String getName()
            {
                return playerIn.getName();
            }
            /**
             * Returns a displayable component representing this thing's name. This method should be implemented
             * slightly differently depending on the interface (for <a href="https://github.com/ModCoderPack/MCPBot-
             * Issues/issues/14">technical reasons</a> the same method is used for both IWorldNameable and
             * ICommandSender), but unlike {@link #getName()} this method will generally behave sanely.
             *  
             * <dl>
             * <dt>{@link net.minecraft.util.INameable#getDisplayName() INameable.getDisplayName()}</dt>
             * <dd>A normal component. Might be a translation component or a text component depending on the context.
             * Usually implemented as:</dd>
             * <dd><pre><code>return this.{@link net.minecraft.util.INameable#hasCustomName() hasCustomName()} ? new
             * TextComponentString(this.{@link #getName()}) : new TextComponentTranslation(this.{@link
             * #getName()});</code></pre></dd>
             * <dt>{@link net.minecraft.command.ICommandSender#getDisplayName() ICommandSender.getDisplayName()} and
             * {@link net.minecraft.entity.Entity#getDisplayName() Entity.getDisplayName()}</dt>
             * <dd>For most entities, this returns the result of {@link #getName()}, with {@linkplain
             * net.minecraft.scoreboard.ScorePlayerTeam#formatPlayerName scoreboard formatting} and a {@linkplain
             * net.minecraft.entity.Entity#getHoverEvent special hover event}.</dd>
             * <dd>For non-entity command senders, this will return the result of {@link #getName()} in a text
             * component.</dd>
             * </dl>
             */
            public ITextComponent getDisplayName()
            {
                return playerIn.getDisplayName();
            }
            /**
             * Send a chat message to the CommandSender
             */
            public void sendMessage(ITextComponent component)
            {
            }
            /**
             * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
             */
            public boolean canUseCommand(int permLevel, String commandName)
            {
                return permLevel <= 2;
            }
            /**
             * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
             * return the coordinates 0, 0, 0
             */
            public BlockPos getPosition()
            {
                return TileEntitySign.this.pos;
            }
            /**
             * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
             * return 0.0D, 0.0D, 0.0D
             */
            public Vec3d getPositionVector()
            {
                return new Vec3d((double)TileEntitySign.this.pos.getX() + 0.5D, (double)TileEntitySign.this.pos.getY() + 0.5D, (double)TileEntitySign.this.pos.getZ() + 0.5D);
            }
            /**
             * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
             * return the overworld
             */
            public World getEntityWorld()
            {
                return playerIn.getEntityWorld();
            }
            /**
             * Returns the entity associated with the command sender. MAY BE NULL!
             */
            public Entity getCommandSenderEntity()
            {
                return playerIn;
            }
            /**
             * Returns true if the command sender should be sent feedback about executed commands
             */
            public boolean sendCommandFeedback()
            {
                return false;
            }
            public void setCommandStat(CommandResultStats.Type type, int amount)
            {
                if (TileEntitySign.this.world != null && !TileEntitySign.this.world.isRemote)
                {
                    TileEntitySign.this.stats.setCommandStatForSender(TileEntitySign.this.world.getMinecraftServer(), this, type, amount);
                }
            }
            /**
             * Get the Minecraft server instance
             */
            public MinecraftServer getServer()
            {
                return playerIn.getServer();
            }
        };

        for (ITextComponent itextcomponent : this.signText)
        {
            Style style = itextcomponent == null ? null : itextcomponent.getStyle();

            if (style != null && style.getClickEvent() != null)
            {
                ClickEvent clickevent = style.getClickEvent();

                if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND)
                {
                    playerIn.getServer().getCommandManager().executeCommand(icommandsender, clickevent.getValue());
                }
            }
        }

        return true;
    }

    public CommandResultStats getStats()
    {
        return this.stats;
    }
}