package net.minecraft.advancements;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Arrays;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;

public class AdvancementRewards
{
    public static final AdvancementRewards EMPTY = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], FunctionObject.CacheableFunction.EMPTY);
    private final int experience;
    private final ResourceLocation[] loot;
    private final ResourceLocation[] recipes;
    private final FunctionObject.CacheableFunction function;

    public AdvancementRewards(int experience, ResourceLocation[] loot, ResourceLocation[] recipes, FunctionObject.CacheableFunction function)
    {
        this.experience = experience;
        this.loot = loot;
        this.recipes = recipes;
        this.function = function;
    }

    public void apply(final EntityPlayerMP player)
    {
        player.addExperience(this.experience);
        LootContext lootcontext = (new LootContext.Builder(player.getServerWorld())).withLootedEntity(player).withPlayer(player).withLuck(player.getLuck()).build(); // Forge: add player & luck to LootContext
        boolean flag = false;

        for (ResourceLocation resourcelocation : this.loot)
        {
            for (ItemStack itemstack : player.world.getLootTableManager().getLootTableFromLocation(resourcelocation).generateLootForPools(player.getRNG(), lootcontext))
            {
                if (player.addItemStackToInventory(itemstack))
                {
                    player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    flag = true;
                }
                else
                {
                    EntityItem entityitem = player.dropItem(itemstack, false);

                    if (entityitem != null)
                    {
                        entityitem.setNoPickupDelay();
                        entityitem.setOwner(player.getName());
                    }
                }
            }
        }

        if (flag)
        {
            player.inventoryContainer.detectAndSendChanges();
        }

        if (this.recipes.length > 0)
        {
            player.unlockRecipes(this.recipes);
        }

        final MinecraftServer minecraftserver = player.server;
        FunctionObject functionobject = this.function.get(minecraftserver.getFunctionManager());

        if (functionobject != null)
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
                    return player.getName();
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
                    return player.getDisplayName();
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
                 * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the
                 * world, return the coordinates 0, 0, 0
                 */
                public BlockPos getPosition()
                {
                    return player.getPosition();
                }
                /**
                 * Get the position vector. <b>{@code null} is not allowed!</b> If you are not an entity in the world,
                 * return 0.0D, 0.0D, 0.0D
                 */
                public Vec3d getPositionVector()
                {
                    return player.getPositionVector();
                }
                /**
                 * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the
                 * world, return the overworld
                 */
                public World getEntityWorld()
                {
                    return player.world;
                }
                /**
                 * Returns the entity associated with the command sender. MAY BE NULL!
                 */
                public Entity getCommandSenderEntity()
                {
                    return player;
                }
                /**
                 * Returns true if the command sender should be sent feedback about executed commands
                 */
                public boolean sendCommandFeedback()
                {
                    return minecraftserver.worlds[0].getGameRules().getBoolean("commandBlockOutput");
                }
                public void setCommandStat(CommandResultStats.Type type, int amount)
                {
                    player.setCommandStat(type, amount);
                }
                /**
                 * Get the Minecraft server instance
                 */
                public MinecraftServer getServer()
                {
                    return player.getServer();
                }
            };
            minecraftserver.getFunctionManager().execute(functionobject, icommandsender);
        }
    }

    public String toString()
    {
        return "AdvancementRewards{experience=" + this.experience + ", loot=" + Arrays.toString((Object[])this.loot) + ", recipes=" + Arrays.toString((Object[])this.recipes) + ", function=" + this.function + '}';
    }

    public static class Deserializer implements JsonDeserializer<AdvancementRewards>
        {
            public AdvancementRewards deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
            {
                JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "rewards");
                int i = JsonUtils.getInt(jsonobject, "experience", 0);
                JsonArray jsonarray = JsonUtils.getJsonArray(jsonobject, "loot", new JsonArray());
                ResourceLocation[] aresourcelocation = new ResourceLocation[jsonarray.size()];

                for (int j = 0; j < aresourcelocation.length; ++j)
                {
                    aresourcelocation[j] = new ResourceLocation(JsonUtils.getString(jsonarray.get(j), "loot[" + j + "]"));
                }

                JsonArray jsonarray1 = JsonUtils.getJsonArray(jsonobject, "recipes", new JsonArray());
                ResourceLocation[] aresourcelocation1 = new ResourceLocation[jsonarray1.size()];

                for (int k = 0; k < aresourcelocation1.length; ++k)
                {
                    aresourcelocation1[k] = new ResourceLocation(JsonUtils.getString(jsonarray1.get(k), "recipes[" + k + "]"));
                    IRecipe irecipe = CraftingManager.getRecipe(aresourcelocation1[k]);

                    if (irecipe == null)
                    {
                        throw new JsonSyntaxException("Unknown recipe '" + aresourcelocation1[k] + "'");
                    }
                }

                FunctionObject.CacheableFunction functionobject$cacheablefunction;

                if (jsonobject.has("function"))
                {
                    functionobject$cacheablefunction = new FunctionObject.CacheableFunction(new ResourceLocation(JsonUtils.getString(jsonobject, "function")));
                }
                else
                {
                    functionobject$cacheablefunction = FunctionObject.CacheableFunction.EMPTY;
                }

                return new AdvancementRewards(i, aresourcelocation, aresourcelocation1, functionobject$cacheablefunction);
            }
        }
}