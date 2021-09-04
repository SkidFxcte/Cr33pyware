package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockAnvil extends BlockFalling
{
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 2);
    protected static final AxisAlignedBB X_AXIS_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.125D, 1.0D, 1.0D, 0.875D);
    protected static final AxisAlignedBB Z_AXIS_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.0D, 0.875D, 1.0D, 1.0D);
    protected static final Logger LOGGER = LogManager.getLogger();

    protected BlockAnvil()
    {
        super(Material.ANVIL);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(DAMAGE, Integer.valueOf(0)));
        this.setLightOpacity(0);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    /**
     * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    /**
     * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
     * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
     * <p>
     * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that
     * does not fit the other descriptions and will generally cause other things not to connect to the face.
     * 
     * @return an approximation of the form of the given face
     * @deprecated call via {@link IBlockState#getBlockFaceShape(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
     * Implementing/overriding is fine.
     */
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     * @deprecated call via {@link IBlockState#isOpaqueCube()} whenever possible. Implementing/overriding is fine.
     */
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        EnumFacing enumfacing = placer.getHorizontalFacing().rotateY();

        try
        {
            return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(FACING, enumfacing).withProperty(DAMAGE, Integer.valueOf(meta >> 2));
        }
        catch (IllegalArgumentException var11)
        {
            if (!worldIn.isRemote)
            {
                LOGGER.warn(String.format("Invalid damage property for anvil at %s. Found %d, must be in [0, 1, 2]", pos, meta >> 2));

                if (placer instanceof EntityPlayer)
                {
                    placer.sendMessage(new TextComponentTranslation("Invalid damage property. Please pick in [0, 1, 2]", new Object[0]));
                }
            }

            return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, 0, placer).withProperty(FACING, enumfacing).withProperty(DAMAGE, Integer.valueOf(0));
        }
    }

    /**
     * Called when the block is right clicked by a player.
     */
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            playerIn.displayGui(new BlockAnvil.Anvil(worldIn, pos));
        }

        return true;
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state)
    {
        return ((Integer)state.getValue(DAMAGE)).intValue();
    }

    /**
     * @deprecated call via {@link IBlockState#getBoundingBox(IBlockAccess,BlockPos)} whenever possible.
     * Implementing/overriding is fine.
     */
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        return enumfacing.getAxis() == EnumFacing.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(this));
        items.add(new ItemStack(this, 1, 1));
        items.add(new ItemStack(this, 1, 2));
    }

    protected void onStartFalling(EntityFallingBlock fallingEntity)
    {
        fallingEntity.setHurtEntities(true);
    }

    public void onEndFalling(World worldIn, BlockPos pos, IBlockState fallingState, IBlockState hitState)
    {
        worldIn.playEvent(1031, pos, 0);
    }

    public void onBroken(World worldIn, BlockPos pos)
    {
        worldIn.playEvent(1029, pos, 0);
    }

    /**
     * @deprecated call via {@link IBlockState#shouldSideBeRendered(IBlockAccess,BlockPos,EnumFacing)} whenever
     * possible. Implementing/overriding is fine.
     */
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3)).withProperty(DAMAGE, Integer.valueOf((meta & 15) >> 2));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
        i = i | ((Integer)state.getValue(DAMAGE)).intValue() << 2;
        return i;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.getBlock() != this ? state : state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, DAMAGE});
    }

    public static class Anvil implements IInteractionObject
        {
            private final World world;
            private final BlockPos position;

            public Anvil(World worldIn, BlockPos pos)
            {
                this.world = worldIn;
                this.position = pos;
            }

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
                return "anvil";
            }

            /**
             * Checks if this thing has a custom name. This method has slightly different behavior depending on the
             * interface (for <a href="https://github.com/ModCoderPack/MCPBot-Issues/issues/14">technical reasons</a>
             * the same method is used for both IWorldNameable and Entity):
             *  
             * <dl>
             * <dt>{@link net.minecraft.util.INameable#hasCustomName() INameable.hasCustomName()}</dt>
             * <dd>If true, then {@link #getName()} probably returns a preformatted name; otherwise, it probably returns
             * a translation string. However, exact behavior varies.</dd>
             * <dt>{@link net.minecraft.entity.Entity#hasCustomName() Entity.hasCustomName()}</dt>
             * <dd>If true, then {@link net.minecraft.entity.Entity#getCustomNameTag() Entity.getCustomNameTag()} will
             * return a non-empty string, which will be used by {@link #getName()}.</dd>
             * </dl>
             */
            public boolean hasCustomName()
            {
                return false;
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
                return new TextComponentTranslation(Blocks.ANVIL.getTranslationKey() + ".name", new Object[0]);
            }

            public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
            {
                return new ContainerRepair(playerInventory, this.world, this.position, playerIn);
            }

            public String getGuiID()
            {
                return "minecraft:anvil";
            }
        }
}