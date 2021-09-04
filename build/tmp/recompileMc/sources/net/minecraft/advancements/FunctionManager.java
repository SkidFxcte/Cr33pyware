package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FunctionManager implements ITickable
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final File functionDir;
    private final MinecraftServer server;
    private final Map<ResourceLocation, FunctionObject> functions = Maps.<ResourceLocation, FunctionObject>newHashMap();
    private String currentGameLoopFunctionId = "-";
    private FunctionObject gameLoopFunction;
    private final ArrayDeque<FunctionManager.QueuedCommand> commandQueue = new ArrayDeque<FunctionManager.QueuedCommand>();
    private boolean isExecuting = false;
    private final ICommandSender gameLoopFunctionSender = new ICommandSender()
    {
        /**
         * Gets the name of this thing. This method has slightly different behavior depending on the interface (for <a
         * href="https://github.com/ModCoderPack/MCPBot-Issues/issues/14">technical reasons</a> the same method is used
         * for both IWorldNameable and ICommandSender):
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
         * net.minecraft.inventory.InventoryCraftResult InventoryCraftResult} (always returns <code>Result</code>) and
         * the {@link net.minecraft.entity.item.EntityMinecart EntityMinecart} family (uses the entity definition). This
         * is not an exaustive list.</dd>
         * <dd>In general, this method should be safe to use on tile entities that implement IInventory.</dd>
         * <dt>{@link net.minecraft.command.ICommandSender#getName() ICommandSender.getName()} and {@link
         * net.minecraft.entity.Entity#getName() Entity.getName()}</dt>
         * <dd>Returns a valid, displayable name (which may be localized). For most entities, this is the translated
         * version of its translation string (obtained via {@link net.minecraft.entity.EntityList#getEntityString
         * EntityList.getEntityString}).</dd>
         * <dd>If this entity has a custom name set, this will return that name.</dd>
         * <dd>For some entities, this will attempt to translate a nonexistent translation string; see <a
         * href="https://bugs.mojang.com/browse/MC-68446">MC-68446</a>. For {@linkplain
         * net.minecraft.entity.player.EntityPlayer#getName() players} this returns the player's name. For {@linkplain
         * net.minecraft.entity.passive.EntityOcelot ocelots} this may return the translation of
         * <code>entity.Cat.name</code> if it is tamed. For {@linkplain net.minecraft.entity.item.EntityItem#getName()
         * item entities}, this will attempt to return the name of the item in that item entity. In all cases other than
         * players, the custom name will overrule this.</dd>
         * <dd>For non-entity command senders, this will return some arbitrary name, such as "Rcon" or "Server".</dd>
         * </dl>
         */
        public String getName()
        {
            return FunctionManager.this.currentGameLoopFunctionId;
        }
        /**
         * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
         */
        public boolean canUseCommand(int permLevel, String commandName)
        {
            return permLevel <= 2;
        }
        /**
         * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
         * return the overworld
         */
        public World getEntityWorld()
        {
            return FunctionManager.this.server.worlds[0];
        }
        /**
         * Get the Minecraft server instance
         */
        public MinecraftServer getServer()
        {
            return FunctionManager.this.server;
        }
    };

    public FunctionManager(@Nullable File functionDirIn, MinecraftServer serverIn)
    {
        this.functionDir = functionDirIn;
        this.server = serverIn;
        this.reload();
    }

    @Nullable
    public FunctionObject getFunction(ResourceLocation id)
    {
        return this.functions.get(id);
    }

    public ICommandManager getCommandManager()
    {
        return this.server.getCommandManager();
    }

    public int getMaxCommandChainLength()
    {
        return this.server.worlds[0].getGameRules().getInt("maxCommandChainLength");
    }

    public Map<ResourceLocation, FunctionObject> getFunctions()
    {
        return this.functions;
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        String s = this.server.worlds[0].getGameRules().getString("gameLoopFunction");

        if (!s.equals(this.currentGameLoopFunctionId))
        {
            this.currentGameLoopFunctionId = s;
            this.gameLoopFunction = this.getFunction(new ResourceLocation(s));
        }

        if (this.gameLoopFunction != null)
        {
            this.execute(this.gameLoopFunction, this.gameLoopFunctionSender);
        }
    }

    public int execute(FunctionObject function, ICommandSender sender)
    {
        int i = this.getMaxCommandChainLength();

        if (this.isExecuting)
        {
            if (this.commandQueue.size() < i)
            {
                this.commandQueue.addFirst(new FunctionManager.QueuedCommand(this, sender, new FunctionObject.FunctionEntry(function)));
            }

            return 0;
        }
        else
        {
            int l;

            try
            {
                this.isExecuting = true;
                int j = 0;
                FunctionObject.Entry[] afunctionobject$entry = function.getEntries();

                for (int k = afunctionobject$entry.length - 1; k >= 0; --k)
                {
                    this.commandQueue.push(new FunctionManager.QueuedCommand(this, sender, afunctionobject$entry[k]));
                }

                while (true)
                {
                    if (this.commandQueue.isEmpty())
                    {
                        l = j;
                        return l;
                    }

                    (this.commandQueue.removeFirst()).execute(this.commandQueue, i);
                    ++j;

                    if (j >= i)
                    {
                        break;
                    }
                }

                l = j;
            }
            finally
            {
                this.commandQueue.clear();
                this.isExecuting = false;
            }

            return l;
        }
    }

    public void reload()
    {
        this.functions.clear();
        this.gameLoopFunction = null;
        this.currentGameLoopFunctionId = "-";
        this.loadFunctions();
    }

    private void loadFunctions()
    {
        if (this.functionDir != null)
        {
            this.functionDir.mkdirs();

            for (File file1 : FileUtils.listFiles(this.functionDir, new String[] {"mcfunction"}, true))
            {
                String s = FilenameUtils.removeExtension(this.functionDir.toURI().relativize(file1.toURI()).toString());
                String[] astring = s.split("/", 2);

                if (astring.length == 2)
                {
                    ResourceLocation resourcelocation = new ResourceLocation(astring[0], astring[1]);

                    try
                    {
                        this.functions.put(resourcelocation, FunctionObject.create(this, Files.readLines(file1, StandardCharsets.UTF_8)));
                    }
                    catch (Throwable throwable)
                    {
                        LOGGER.error("Couldn't read custom function " + resourcelocation + " from " + file1, throwable);
                    }
                }
            }

            if (!this.functions.isEmpty())
            {
                LOGGER.info("Loaded " + this.functions.size() + " custom command functions");
            }
        }
    }

    public static class QueuedCommand
        {
            private final FunctionManager functionManager;
            private final ICommandSender sender;
            private final FunctionObject.Entry entry;

            public QueuedCommand(FunctionManager functionManagerIn, ICommandSender senderIn, FunctionObject.Entry entryIn)
            {
                this.functionManager = functionManagerIn;
                this.sender = senderIn;
                this.entry = entryIn;
            }

            public void execute(ArrayDeque<FunctionManager.QueuedCommand> commandQueue, int maxCommandChainLength)
            {
                this.entry.execute(this.functionManager, this.sender, commandQueue, maxCommandChainLength);
            }

            public String toString()
            {
                return this.entry.toString();
            }
        }
}