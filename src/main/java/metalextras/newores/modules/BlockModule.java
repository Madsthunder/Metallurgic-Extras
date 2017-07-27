package metalextras.newores.modules;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.bind.TypeAdapters;
import api.metalextras.OreTypes;
import api.metalextras.OreUtils;
import metalextras.MetalExtras;
import metalextras.newores.NewOreType;
import metalextras.newores.VariableManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BlockModule extends OreModule<NewOreType, BlockModule>
{
    protected final Map<OreTypes, ResourceLocation> name_overrides = Maps.newHashMap();
    protected int harvest_level = 0;
    protected BlockModule.Drop[] drops = new BlockModule.Drop[0];
    protected int min_xp = 0;
    protected int max_xp = 0;
    protected CreativeTabs[] creative_tabs = new CreativeTabs[0];
    
    public BlockModule(String path, JsonObject block_object, boolean parse)
    {
    	super(path, NewOreType.class, BlockModule.class, block_object);
    	if(parse)
    	{
    		JsonObject overrides_object = JsonUtils.getJsonObject(block_object, "name_overrides", new JsonObject());
            for(Entry<String, JsonElement> entry : overrides_object.entrySet())
            {
            	ResourceLocation override_key = new ResourceLocation(entry.getKey());
            	JsonElement override_element = entry.getValue();
            	if(override_element.isJsonPrimitive() && override_element.getAsJsonPrimitive().isString())
            	{
            		OreTypes materials = OreUtils.getTypeCollectionsRegistry().getValue(override_key);
            		if(materials == null)
                        MetalExtras.LOGGER.warn(String.format("The element at %s/name_overrides/%s references a non-existent OreMaterials entry. (%s)", path, override_key, override_key));
            		else
            			this.name_overrides.put(materials, new ResourceLocation(override_element.getAsString()));
            	}
            	else
                    MetalExtras.LOGGER.warn(String.format("The element at %s/name_overrides/%s must be a JsonPrimitive String.", path, override_key));
            }
            this.harvest_level = JsonUtils.getInt(block_object, "harvest_level", 0);
            JsonArray drops_array = JsonUtils.getJsonArray(block_object, "drops", new JsonArray());
            List<Drop> drops = Lists.newArrayList();
            for(int i = 0; i < drops_array.size(); i++)
            {
                JsonObject drop_object = drops_array.get(i).getAsJsonObject();
                if(!drop_object.has("item"))
                {
                    MetalExtras.LOGGER.warn(String.format("The object at %s/drops[%s] is missing an \"item\" JsonPrimitive.", path, i));
                    continue;
                }
                Drop drop = new Drop();
                JsonElement item_element = drop_object.get("item");
                if(item_element.isJsonPrimitive() && item_element.getAsJsonPrimitive().isString())
                {
                    String item_string = item_element.getAsString();
                    if(item_string.startsWith("#"))
                        drop.item = VariableManager.warnIfAbsent(VariableManager.getConstant(new ResourceLocation(item_string.substring(1)), Item.class), Items.AIR, "The string at %s/drops[%s]/item references a non-existent constant (%s). Defaulting to air.", path, i, item_string);
                    else
                        drop.item = VariableManager.warnIfAbsent(Optional.ofNullable(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item_string))), Items.AIR, "The string at %s/drops[%s]/item references an unregistered item (%s). Defaulting to air.", path, i, item_string);
                }
                if(drop.item == Items.AIR)
                {
                    MetalExtras.LOGGER.warn(String.format("The object at %s/drops[%s] doesn't have an item or has an item that is invalid.", path, i));
                    continue;
                }
                JsonElement metadata_element = Optional.ofNullable(drop_object.get("data")).orElseGet(() -> new JsonPrimitive(0));
                JsonPrimitive metadata_primitive;
                if(metadata_element.isJsonPrimitive() && ((metadata_primitive = metadata_element.getAsJsonPrimitive()).isString() || metadata_primitive.isNumber()))
                {
                    if(metadata_primitive.isNumber())
                        drop.metadata = metadata_primitive.getAsInt();
                    else if(metadata_primitive.isString())
                    {
                        String metadata_string = metadata_primitive.getAsString();
                        if(metadata_string.startsWith("#"))
                            drop.metadata = VariableManager.warnIfAbsent(VariableManager.getNumberConstant(new ResourceLocation(metadata_string.substring(1))), 0, "The string at %s/drops[%s]/data references a non-existent constant (%s)", path, i, metadata_string).intValue();
                    }
                    else
                    {
                        //TODO throw exception
                    }
                }
                else
                    MetalExtras.LOGGER.warn(String.format("The element at %s/drops[%s]/data must be a JsonPrimitive Number or String.", path, i));
                try
                {
                    NBTTagCompound nbt = JsonToNBT.getTagFromJson(TypeAdapters.JSON_ELEMENT.toJson(JsonUtils.getJsonObject(drop_object, "nbt", new JsonObject())));
                    if(nbt.hasKey("ForgeCaps"))
                    {
                        drop.nbt.setTag("ForgeCaps", nbt.getCompoundTag("ForgeCaps"));
                        nbt.removeTag("ForgeCaps");
                    }
                    drop.nbt.setTag("tag", nbt);
                }
                catch(Exception e)
                {
                	//TODO I wanna be a real error! (Yes, that's a pinnochio reference)
                	e.printStackTrace();
                }
                JsonElement count_element = drop_object.get("count");
                if(count_element == null)
                {
                    drop.chances = new float[] { 1 };
                    drop.min_counts = new int[] { 1 };
                    drop.max_counts = new int[] { 1 };
                }
                else if(count_element.isJsonObject())
                {
                    JsonObject count_object = count_element.getAsJsonObject();
                    drop.chances = new float[] { Math.min(JsonUtils.getFloat(count_object, "chance", 1F),  1F) };
                    drop.min_counts = new int[] { JsonUtils.getInt(count_object, "min", 1) };
                    drop.max_counts = new int[] { JsonUtils.getInt(count_object, "max", drop.min_counts[0]) };
                }
                else if(count_element.isJsonArray())
                {
                    JsonArray array = count_element.getAsJsonArray();
                    drop.chances = new float[array.size()];
                    drop.min_counts = new int[array.size()];
                    drop.max_counts = new int[array.size()];
                    for(int j = 0; j < array.size(); j++)
                    {
                        JsonElement count_element1 = array.get(i);
                        if(count_element1.isJsonObject())
                        {
                            JsonObject count_object1 = count_element1.getAsJsonObject();
                            drop.chances[j] = Math.min(JsonUtils.getFloat(count_object1, "chance", 1F), 1F);
                            drop.min_counts[j] = JsonUtils.getInt(count_object1, "min", 1);
                            drop.max_counts[j] =  JsonUtils.getInt(count_object1, "max", drop.min_counts[j]);
                        }
                        else if(count_element1.isJsonPrimitive())
                        {
                            drop.chances[j] = 1F;
                            drop.min_counts[j] = drop.max_counts[j] = count_element1.getAsInt();
                        }
                    }
                }
                drops.add(drop);
            }
            this.drops = Iterables.toArray(drops, Drop.class);
            JsonElement xp_element = block_object.has("xp") ? block_object.get("xp") : new JsonObject();
            if(xp_element.isJsonObject())
            {
                JsonObject xp_object = xp_element.getAsJsonObject();
                this.min_xp = JsonUtils.getInt(xp_object, "min", 0);
                this.max_xp = JsonUtils.getInt(xp_object, "max", this.min_xp);
            }
            else if(xp_element.isJsonPrimitive())
            {
            	this.min_xp = 0;
                this.max_xp = xp_element.getAsInt();
            }
            else
                MetalExtras.LOGGER.warn(String.format("The object at %s/xp is expected to be a JsonPrimitive or a JsonObject.", path));
            JsonArray block_creative_tab_array = JsonUtils.getJsonArray(block_object, "creative_tabs", new JsonArray());
            List<CreativeTabs> block_creative_tab_list = Lists.newArrayList();
            for(int i = 0; i < block_creative_tab_array.size(); i++)
                block_creative_tab_list.addAll(Lists.newArrayList(OreUtils.getCreativeTabs(block_creative_tab_array.get(i).getAsString())));
            this.creative_tabs = Iterables.toArray(block_creative_tab_list, CreativeTabs.class);
    	}
    }
    
    public final int getHarvestLevel()
    {
        return this.harvest_level;
    }
    
    public final BlockModule.Drop[] getDrops()
    {
        return this.drops;
    }
    
    public final int getMinXp()
    {
        return this.min_xp;
    }
    
    public final int getMaxXp()
    {
        return this.max_xp;
    }
    
    public final CreativeTabs[] getCreativeTabs()
    {
        return this.creative_tabs;
    }
    
    public final boolean hasNameOverride(OreTypes materials)
    {
    	return this.name_overrides.containsKey(materials);
    }
    
    public final ResourceLocation getNameOverride(OreTypes materials)
    {
    	return this.name_overrides.get(materials);
    }
    
    public static class Drop
    {
        protected Item item = Items.AIR;
        protected int metadata = 0;
        protected NBTTagCompound nbt = new NBTTagCompound();
        protected float[] chances = new float[] { 1F } ;
        protected int[] min_counts = new int[] { 1 };
        protected int[] max_counts = { 1 };
        
        public Drop()
        {
        	super();
        }
        
        public final Item getItem()
        {
            return this.item;
        }
        
        public final int getMetadata()
        {
            return this.metadata;
        }
        
        public final NBTTagCompound getNbt()
        {
            return this.nbt.copy();
        }
        
        public final float getChance(int fortune)
        {
            return this.chances[Math.min(fortune, this.chances.length - 1)];
        }
        
        public final int getMinCount(int fortune)
        {
            return this.min_counts[Math.min(fortune, this.min_counts.length - 1)];
        }
        
        public final int getMaxCount(int fortune)
        {
            return this.max_counts[Math.min(fortune, this.max_counts.length - 1)];
        }
    }
}