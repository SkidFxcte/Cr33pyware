package net.minecraft.command;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class CommandResultStats
{
    /** The number of result command result types that are possible. */
    private static final int NUM_RESULT_TYPES = CommandResultStats.Type.values().length;
    private static final String[] STRING_RESULT_TYPES = new String[NUM_RESULT_TYPES];
    /** List of entityID who set a stat, username for a player, UUID for all entities */
    private String[] entitiesID;
    /** List of all the Objectives names */
    private String[] objectives;

    public CommandResultStats()
    {
        this.entitiesID = STRING_RESULT_TYPES;
        this.objectives = STRING_RESULT_TYPES;
    }

    public void setCommandStatForSender(MinecraftServer server, final ICommandSender sender, CommandResultStats.Type typeIn, int p_184932_4_)
    {
        String s = this.entitiesID[typeIn.getTypeID()];

        if (s != null)
        {
            ICommandSender icommandsender = new ICommandSender()
            {
                /**
                 * Gets the name of this thing. This method has slightly different behavior depending on the interface
                 * (for <a href="https://github.com/ModCoderPack/MCPBot-Issues/issues/14">technical reasons</a> the same
                 * method is used for both IWorldNameable and ICommandSender):
                 *  
                 * <dl>
                 * <dt>{@link net.minecraft.util.INameable#getName() INameable.getName()}</dt>
                 * <dd>Returns the name of this inventory. If this {@linkplain net.minecraft.inventory#hasCustomName()
                 * has a custom name} then this <em>should</em> be a direct string; otherwise it <em>should</em> be a
                 * valid translation string.</dd>
                 * <dd>However, note that <strong>the translation string may be invalid</strong>, as is the case for
                 * {@link net.minecraft.tileentity.TileEntityBanner TileEntityBanner} (always returns nonexistent
                 * translation code <code>banner</code> without a custom name), {@link
                 * net.minecraft.block.BlockAnvil.Anvil BlockAnvil$Anvil} (always returns <code>anvil</code>), {@link
                 * net.minecraft.block.BlockWorkbench.InterfaceCraftingTable BlockWorkbench$InterfaceCraftingTable}
                 * (always returns <code>crafting_table</code>), {@link net.minecraft.inventory.InventoryCraftResult
                 * InventoryCraftResult} (always returns <code>Result</code>) and the {@link
                 * net.minecraft.entity.item.EntityMinecart EntityMinecart} family (uses the entity definition). This is
                 * not an exaustive list.</dd>
                 * <dd>In general, this method should be safe to use on tile entities that implement IInventory.</dd>
                 * <dt>{@link net.minecraft.command.ICommandSender#getName() ICommandSender.getName()} and {@link
                 * net.minecraft.entity.Entity#getName() Entity.getName()}</dt>
                 * <dd>Returns a valid, displayable name (which may be localized). For most entities, this is the
                 * translated version of its translation string (obtained via {@link
                 * net.minecraft.entity.EntityList#getEntityString EntityList.getEntityString}).</dd>
                 * <dd>If this entity has a custom name set, this will return that name.</dd>
                 * <dd>For some entities, this will attempt to translate a nonexistent translation string; see <a
                 * href="https://bugs.mojang.com/browse/MC-68446">MC-68446</a>. For {@linkplain
                 * net.minecraft.entity.player.EntityPlayer#getName() players} this returns the player's name. For
                 * {@linkplain net.minecraft.entity.passive.EntityOcelot ocelots} this may return the translation of
                 * <code>entity.Cat.name</code> if it is tamed. For {@linkplain
                 * net.minecraft.entity.item.EntityItem#getName() item entities}, this will attempt to return the name
                 * of the item in that item entity. In all cases other than players, the custom name will overrule
                 * this.</dd>
                 * <dd>For non-entity command senders, this will return some arbitrary name, such as "Rcon" or
                 * "Server".</dd>
                 * </dl>
                 */
                public String getName()
                {
                    return sender.getName();
                }
                /**
                 * Returns a displayable component representing this thing's name. This method should be implemented
                 * slightly differently depending on the interface (for <a href="https://github.com/ModCoderPack/MCPBot-
                 * Issues/issues/14">technical reasons</a> the same method is used for both IWorldNameable and
                 * ICommandSender), but unlike {@link #getName()} this method will generally behave sanely.
                 *  
                 * <dl>
                 * <dt>{@link net.minecraft.util.INameable#getDisplayName() INameable.getDisplayName()}</dt>
                 * <dd>A normal component. Might be a translation component or a text component depending on the
                 * context. Usually implemented as:</dd>
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
                    return sender.getDisplayName();
                }
                /**
                 * Send a chat message to the CommandSender
                 */
                public void sendMessage(ITextComponent component)
                {
                    sender.sendMessage(component);
                }
                /**
                 * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
                 */
                public boolean canUseCommand(int permLevel, String commandName)
                {
                    return true;
                }
                /**
                 * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the
                 * world, return the coordinates 0, 0, 0
                 */
                public BlockPos getPosition()
                {
                    return sender.getPosition();
                }
                /**
                 * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
                 * return 0.0D, 0.0D, 0.0D
                 */
                public Vec3d getPositionVector()
                {
                    return sender.getPositionVector();
                }
                /**
                 * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the
                 * world, return the overworld
                 */
                public World getEntityWorld()
                {
                    return sender.getEntityWorld();
                }
                /**
                 * Returns the entity associated with the command sender. MAY BE NULL!
                 */
                public Entity getCommandSenderEntity()
                {
                    return sender.getCommandSenderEntity();
                }
                /**
                 * Returns true if the command sender should be sent feedback about executed commands
                 */
                public boolean sendCommandFeedback()
                {
                    return sender.sendCommandFeedback();
                }
                public void setCommandStat(CommandResultStats.Type type, int amount)
                {
                    sender.setCommandStat(type, amount);
                }
                /**
                 * Get the Minecraft server instance
                 */
                public MinecraftServer getServer()
                {
                    return sender.getServer();
                }
            };
            String s1;

            try
            {
                s1 = CommandBase.getEntityName(server, icommandsender, s);
            }
            catch (CommandException var12)
            {
                return;
            }

            String s2 = this.objectives[typeIn.getTypeID()];

            if (s2 != null)
            {
                Scoreboard scoreboard = sender.getEntityWorld().getScoreboard();
                ScoreObjective scoreobjective = scoreboard.getObjective(s2);

                if (scoreobjective != null)
                {
                    if (scoreboard.entityHasObjective(s1, scoreobjective))
                    {
                        Score score = scoreboard.getOrCreateScore(s1, scoreobjective);
                        score.setScorePoints(p_184932_4_);
                    }
                }
            }
        }
    }

    public void readStatsFromNBT(NBTTagCompound tagcompound)
    {
        if (tagcompound.hasKey("CommandStats", 10))
        {
            NBTTagCompound nbttagcompound = tagcompound.getCompoundTag("CommandStats");

            for (CommandResultStats.Type commandresultstats$type : CommandResultStats.Type.values())
            {
                String s = commandresultstats$type.getTypeName() + "Name";
                String s1 = commandresultstats$type.getTypeName() + "Objective";

                if (nbttagcompound.hasKey(s, 8) && nbttagcompound.hasKey(s1, 8))
                {
                    String s2 = nbttagcompound.getString(s);
                    String s3 = nbttagcompound.getString(s1);
                    setScoreBoardStat(this, commandresultstats$type, s2, s3);
                }
            }
        }
    }

    public void writeStatsToNBT(NBTTagCompound tagcompound)
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        for (CommandResultStats.Type commandresultstats$type : CommandResultStats.Type.values())
        {
            String s = this.entitiesID[commandresultstats$type.getTypeID()];
            String s1 = this.objectives[commandresultstats$type.getTypeID()];

            if (s != null && s1 != null)
            {
                nbttagcompound.setString(commandresultstats$type.getTypeName() + "Name", s);
                nbttagcompound.setString(commandresultstats$type.getTypeName() + "Objective", s1);
            }
        }

        if (!nbttagcompound.isEmpty())
        {
            tagcompound.setTag("CommandStats", nbttagcompound);
        }
    }

    /**
     * Set a stat in the scoreboard
     */
    public static void setScoreBoardStat(CommandResultStats stats, CommandResultStats.Type resultType, @Nullable String entityID, @Nullable String objectiveName)
    {
        if (entityID != null && !entityID.isEmpty() && objectiveName != null && !objectiveName.isEmpty())
        {
            if (stats.entitiesID == STRING_RESULT_TYPES || stats.objectives == STRING_RESULT_TYPES)
            {
                stats.entitiesID = new String[NUM_RESULT_TYPES];
                stats.objectives = new String[NUM_RESULT_TYPES];
            }

            stats.entitiesID[resultType.getTypeID()] = entityID;
            stats.objectives[resultType.getTypeID()] = objectiveName;
        }
        else
        {
            removeScoreBoardStat(stats, resultType);
        }
    }

    /**
     * Remove a stat from the scoreboard
     */
    private static void removeScoreBoardStat(CommandResultStats resultStatsIn, CommandResultStats.Type resultTypeIn)
    {
        if (resultStatsIn.entitiesID != STRING_RESULT_TYPES && resultStatsIn.objectives != STRING_RESULT_TYPES)
        {
            resultStatsIn.entitiesID[resultTypeIn.getTypeID()] = null;
            resultStatsIn.objectives[resultTypeIn.getTypeID()] = null;
            boolean flag = true;

            for (CommandResultStats.Type commandresultstats$type : CommandResultStats.Type.values())
            {
                if (resultStatsIn.entitiesID[commandresultstats$type.getTypeID()] != null && resultStatsIn.objectives[commandresultstats$type.getTypeID()] != null)
                {
                    flag = false;
                    break;
                }
            }

            if (flag)
            {
                resultStatsIn.entitiesID = STRING_RESULT_TYPES;
                resultStatsIn.objectives = STRING_RESULT_TYPES;
            }
        }
    }

    /**
     * Add all stats in the CommandResultStats
     */
    public void addAllStats(CommandResultStats resultStatsIn)
    {
        for (CommandResultStats.Type commandresultstats$type : CommandResultStats.Type.values())
        {
            setScoreBoardStat(this, commandresultstats$type, resultStatsIn.entitiesID[commandresultstats$type.getTypeID()], resultStatsIn.objectives[commandresultstats$type.getTypeID()]);
        }
    }

    public static enum Type
    {
        SUCCESS_COUNT(0, "SuccessCount"),
        AFFECTED_BLOCKS(1, "AffectedBlocks"),
        AFFECTED_ENTITIES(2, "AffectedEntities"),
        AFFECTED_ITEMS(3, "AffectedItems"),
        QUERY_RESULT(4, "QueryResult");

        /** The integer ID of the Result Type. */
        final int typeID;
        /** The string representation of the type. */
        final String typeName;

        private Type(int id, String name)
        {
            this.typeID = id;
            this.typeName = name;
        }

        /**
         * Retrieves the integer ID of the result type.
         */
        public int getTypeID()
        {
            return this.typeID;
        }

        /**
         * Retrieves the name of the type.
         */
        public String getTypeName()
        {
            return this.typeName;
        }

        /**
         * Returns the names of all possible Result Types.
         */
        public static String[] getTypeNames()
        {
            String[] astring = new String[values().length];
            int i = 0;

            for (CommandResultStats.Type commandresultstats$type : values())
            {
                astring[i++] = commandresultstats$type.getTypeName();
            }

            return astring;
        }

        /**
         * Retrieves the Type indicated by the supplied name string.
         */
        @Nullable
        public static CommandResultStats.Type getTypeByName(String name)
        {
            for (CommandResultStats.Type commandresultstats$type : values())
            {
                if (commandresultstats$type.getTypeName().equals(name))
                {
                    return commandresultstats$type;
                }
            }

            return null;
        }
    }
}