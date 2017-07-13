package metalextras.newores;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.bind.TypeAdapters;

import api.metalextras.BlockOre;
import api.metalextras.ModelType;
import api.metalextras.OreType;
import api.metalextras.OreTypes;
import api.metalextras.OreUtils;
import metalextras.MetalExtras;
import metalextras.newores.NewOreType.Block.Drop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class NewOreType
{
    public static final String DEFAULT_NAME = "tile.metalextras:unnamed";
    
    public final ResourceLocation registry_name;
    public final NewOreType.Block block;
    public final NewOreType.Generation generation;
    public final NewOreType.Smelting smelting;
    public final NewOreType.Model model;
    protected final Map<OreTypes, BlockOre> blocks = Maps.newHashMap();
    protected String name = DEFAULT_NAME;
    protected CreativeTabs[] item_creative_tabs = new CreativeTabs[0];
    protected String[] ore_dictionary = new String[0];
    
    public NewOreType(ResourceLocation registry_name, NewOreType.Block block, NewOreType.Generation generation, NewOreType.Smelting smelting, NewOreType.Model model)
    {
        this.registry_name = registry_name;
        this.block = block;
        this.generation = generation;
        this.generation.parent = this;
        this.smelting = smelting;
        this.model = model;
    }
    
    public final String getName()
    {
        return this.name;
    }
    
    public final CreativeTabs[] getItemCreativeTabs()
    {
        return this.item_creative_tabs;
    }
    
    public final String[] getOreDictionary()
    {
        return this.ore_dictionary;
    }
    
    public final boolean isJsonOreType()
    {
        return this instanceof JsonOreType;
    }
    
    public final String toString()
    {
        return String.format("OreType{%s}", this.registry_name);
    }
    
    public final BlockOre getBlock(OreTypes types)
    {
        if(this.blocks.containsKey(types))
            return this.blocks.get(types);
        BlockOre block = this.createBlock(types);
        if(ForgeRegistries.BLOCKS.containsKey(block.getRegistryName()))
        {
            net.minecraft.block.Block block1 = ForgeRegistries.BLOCKS.getValue(block.getRegistryName());
            if(block1 instanceof BlockOre)
                block = (BlockOre)block1;
            else
                throw new IllegalStateException("There Is Already A Block \"" + block.getRegistryName() + "\" Registered That Doesn't Extend BlockOre.");
        }
        this.blocks.put(types, block);
        return block;
    }
    
    public Iterable<BlockOre> getBlocksToRegister(OreTypes types)
    {
        return Sets.newHashSet(this.createBlock(types));
    }
    
    public BlockOre createBlock(OreTypes types)
    {
        return new BlockOre(this, types);
    }
    
    public Collection<BlockOre> getBlocks()
    {
        for(OreTypes types : OreUtils.getTypeCollectionsRegistry())
            if(!this.blocks.containsKey(types))
                this.getBlock(types);
        return Sets.newHashSet(this.blocks.values());
    }
    
    @Nullable
    public IBlockState applyBlockState(OreType type)
    {
        for(BlockOre ore : this.blocks.values())
            if(ore.getOreTypeProperty().getTypes().hasOreType(type))
                return ore.getBlockState(type);
        return null;
    }
    
    public static final class JsonOreType extends NewOreType
    {
        public JsonOreType(ResourceLocation registry_name, JsonElement element) throws JsonParseException, NBTException
        {
            super(registry_name, parseBlock(registry_name, element), parseGeneration(registry_name, element.getAsJsonObject()), parseSmelting(registry_name, element.getAsJsonObject()), parseModel(registry_name, element.getAsJsonObject()));
            JsonObject object = element.getAsJsonObject();
            this.name = JsonUtils.getString(object, "name", DEFAULT_NAME);
            JsonArray oredict_array = JsonUtils.getJsonArray(object, "ore_dictionary", new JsonArray());
            List<String> oredict_list = Lists.newArrayList();
            for(int i = 0; i < oredict_array.size(); i++)
                oredict_list.add(oredict_array.get(i).getAsString());
            this.ore_dictionary = Iterables.toArray(oredict_list, String.class);
            JsonObject item = JsonUtils.getJsonObject(object, "item", new JsonObject());
            JsonArray item_creative_tab_array = JsonUtils.getJsonArray(item, "creative_tabs", new JsonArray());
            List<CreativeTabs> item_creative_tab_list = Lists.newArrayList();
            for(int i = 0; i < item_creative_tab_array.size(); i++)
                item_creative_tab_list.addAll(Lists.newArrayList(OreUtils.getCreativeTabs(item_creative_tab_array.get(i).getAsString())));
            this.item_creative_tabs = Iterables.toArray(item_creative_tab_list, CreativeTabs.class);
        }
        
        public static Block parseBlock(ResourceLocation registry_name, JsonElement element) throws JsonParseException, NBTException
        {
            if(!element.isJsonObject())
                throw new JsonParseException("Element is expected to be a JsonObject");
            JsonObject block_object = JsonUtils.getJsonObject(element.getAsJsonObject(), "block", new JsonObject());
            Block block = new Block();
            block.harvest_level = JsonUtils.getInt(block_object, "harvest_level", 0);
            JsonArray drops_array = JsonUtils.getJsonArray(block_object, "drops", new JsonArray());
            List<Drop> drops = Lists.newArrayList();
            for(int i = 0; i < drops_array.size(); i++)
            {
                JsonObject drop_object = drops_array.get(i).getAsJsonObject();
                if(!drop_object.has("item"))
                {
                    MetalExtras.LOGGER.warn("The object at %s/block/drops[%s] is missing an \"item\" JsonPrimitive.", registry_name, i);
                    continue;
                }
                Drop drop = new Drop();
                JsonElement item_element = drop_object.get("item");
                if(item_element.isJsonPrimitive() && item_element.getAsJsonPrimitive().isString())
                {
                    String item_string = item_element.getAsString();
                    if(item_string.startsWith("#"))
                        drop.item = VariableManager.warnIfAbsent(VariableManager.getConstant(new ResourceLocation(item_string.substring(1)), Item.class), Items.AIR, "The string at %s/block/drops[%s]/item references a non-existent constant (%s). Defaulting to air.", registry_name, i, item_string);
                    else
                        drop.item = VariableManager.warnIfAbsent(Optional.ofNullable(ForgeRegistries.ITEMS.getValue(new ResourceLocation(item_string))), Items.AIR, "The string at %s/block/drops[%s]/item references an unregistered item (%s). Defaulting to air.", registry_name, i, item_string);
                }
                if(drop.item == Items.AIR)
                {
                    MetalExtras.LOGGER.warn("The object at %s/block/drops[%s] doesn't have an item or has an item that is invalid.", registry_name, i);
                    continue;
                }
                JsonElement metadata_element = Optional.ofNullable(drop_object.get("data")).orElseGet(() -> new JsonPrimitive(1));
                JsonPrimitive metadata_primitive;
                if(metadata_element.isJsonPrimitive() && ((metadata_primitive = metadata_element.getAsJsonPrimitive()).isString() || metadata_primitive.isNumber()))
                {
                    if(metadata_primitive.isNumber())
                        drop.metadata = metadata_primitive.getAsInt();
                    else if(metadata_primitive.isString())
                    {
                        String metadata_string = metadata_primitive.getAsString();
                        if(metadata_string.startsWith("#"))
                            drop.metadata = VariableManager.warnIfAbsent(VariableManager.getNumberConstant(new ResourceLocation(metadata_string.substring(1))), 0, "The string at %s/block/drops[%s]/data references a non-existent constant (%s)", registry_name, i, metadata_string).intValue();
                    }
                    else
                    {
                        //TODO throw exception
                    }
                }
                else
                    MetalExtras.LOGGER.warn("The element at %s/block/drops[%s]/data must be a number or a string.", registry_name, i);
                NBTTagCompound nbt = JsonToNBT.getTagFromJson(TypeAdapters.JSON_ELEMENT.toJson(JsonUtils.getJsonObject(drop_object, "nbt", new JsonObject())));
                if(nbt.hasKey("ForgeCaps"))
                {
                    drop.nbt.setTag("ForgeCaps", nbt.getCompoundTag("ForgeCaps"));
                    nbt.removeTag("ForgeCaps");
                }
                drop.nbt.setTag("tag", nbt);
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
            block.drops = Iterables.toArray(drops, Drop.class);
            JsonElement xp_element = block_object.has("xp") ? block_object.get("xp") : new JsonObject();
            if(xp_element.isJsonObject())
            {
                JsonObject xp_object = xp_element.getAsJsonObject();
                block.min_xp = JsonUtils.getInt(xp_object, "min", 0);
                block.max_xp = JsonUtils.getInt(xp_object, "max", block.min_xp);
            }
            else if(xp_element.isJsonPrimitive())
            {
                block.min_xp = 0;
                block.max_xp = xp_element.getAsInt();
            }
            else
                MetalExtras.LOGGER.warn("The object at %s/block/xp is expected to be a JsonPrimitive or a JsonObject.", registry_name);
            JsonArray block_creative_tab_array = JsonUtils.getJsonArray(block_object, "creative_tabs", new JsonArray());
            List<CreativeTabs> block_creative_tab_list = Lists.newArrayList();
            for(int i = 0; i < block_creative_tab_array.size(); i++)
                block_creative_tab_list.addAll(Lists.newArrayList(OreUtils.getCreativeTabs(block_creative_tab_array.get(i).getAsString())));
            block.creative_tabs = Iterables.toArray(block_creative_tab_list, CreativeTabs.class);
            return block;
        }
        
        public static Generation parseGeneration(ResourceLocation registry_name, JsonObject object) throws JsonParseException
        {
            JsonObject generation_object = JsonUtils.getJsonObject(object, "generation", new JsonObject());
            JsonElement event_element = generation_object.has("event") ? generation_object.get("event") : new JsonObject();
            GenerateMinable.EventType event;
            boolean post_event;
            if(event_element.isJsonObject())
            {
                JsonObject event_object = JsonUtils.getJsonObject(generation_object, "event", new JsonObject());
                event = OreUtils.getEventType(JsonUtils.getString(event_object, "name", ""));
                post_event = JsonUtils.getBoolean(event_object, "post", true);
            }
            else if(event_element.isJsonPrimitive())
            {
                event = OreUtils.getEventType(event_element.getAsString());
                post_event = true;
            }
            else
            {
                //TODO warning
                event = null;
                post_event = false;
            }
            Generation generation = new Generation(event, post_event);
            generation.tries = JsonUtils.getInt(generation_object, "tries");
            JsonElement height = generation_object.get("height");
            if(height.isJsonObject())
            {
                JsonObject height_object = height.getAsJsonObject();
                generation.min_height = JsonUtils.getInt(height_object, "min", 0);
                generation.max_height = JsonUtils.getInt(height_object, "max", 0);
            }
            else
                generation.max_height = height.getAsInt();
            generation.size = JsonUtils.getInt(generation_object, "size", 1);
            JsonObject temperature_object = JsonUtils.getJsonObject(generation_object, "temperature", new JsonObject());
            generation.min_temperature = JsonUtils.getFloat(temperature_object, "min", -Float.MAX_VALUE);
            generation.max_temperature = JsonUtils.getFloat(temperature_object, "max", Float.MAX_VALUE);
            Set<OreType> materials = Sets.newHashSet(OreUtils.getAllOreTypes());
            JsonElement filters_element = Optional.ofNullable(generation_object.get("filters")).orElseGet(() -> new JsonArray());
            if(filters_element.isJsonArray())
            {
                JsonArray filters_array = filters_element.getAsJsonArray();
                for(int i = 0; i < filters_array.size(); i++)
                {
                    JsonElement filter_element = filters_array.get(i);
                    if(filter_element.isJsonObject())
                    {
                        JsonObject filter_object = filter_element.getAsJsonObject();
                        String type_string = JsonUtils.getString(filter_object, "type");
                        BiConsumer<Set<OreType>, Set<String>> filter = FilterManager.getFilter(new ResourceLocation(type_string));
                        if(filter == null)
                        {
                            MetalExtras.LOGGER.warn("The element at %s/generation/filters[%s]/type references a non-existent filter (%s)", registry_name, i, type_string);
                            continue;
                        }
                        Set<String> param_set = Sets.newHashSet();
                        JsonArray param_array = JsonUtils.getJsonArray(filter_object, "params");
                        for(int j = 0; j < param_array.size(); j++)
                        {
                            JsonElement param_element = param_array.get(j);
                            if(param_element.isJsonPrimitive() && param_element.getAsJsonPrimitive().isString())
                                param_set.add(param_element.getAsString());
                            else
                                MetalExtras.LOGGER.warn("The element at %s/generation/filters[%s]/params[%s] must be a string", registry_name, i, j);
                        }
                        filter.accept(materials, param_set);
                    }
                    else
                        MetalExtras.LOGGER.warn("The element at %s/generation/filters[%s] must be a JsonObject", registry_name, i);
                }
            }
            for(OreType material : materials)
                generation.allowed_states.put(material.getState(), material);
            List<String> reasons = Lists.newArrayList();
            if(generation.tries <= 0)
                reasons.add(String.format("spawn %s times per chunk", generation.tries));
            if(generation.max_height <= 0)
                reasons.add("generate at a maximum height of 0 or below");
            if(generation.max_height <= generation.min_height)
                reasons.add("generate at a maximum height that is lower than its minimum height");
            if(generation.size <= 0)
                reasons.add(String.format("generate with a maximum vein size of %s", generation.size));
            if(generation.min_temperature > generation.max_temperature)
                reasons.add(String.format("generate at a place in which its temperature is greater than %s but less than %s", generation.min_temperature, generation.max_temperature));
            if(!reasons.isEmpty())
            {
                String reasons_string;
                if(reasons.size() > 1)
                    if(reasons.size() == 2)
                        reasons_string = String.format("%s and %s", reasons.get(0), reasons.get(1));
                    else
                    {
                        String last = reasons.remove(reasons.size() - 1);
                        reasons_string = String.format("%s, and %s", Joiner.on(", ").join(reasons), last);
                    }
                else
                    reasons_string = reasons.get(0);
                MetalExtras.LOGGER.warn("The ore %s will not be able to generate because the ore is set to %s.", registry_name, reasons_string);
            }
            return generation;
        }
        
        public static Smelting parseSmelting(ResourceLocation registry_name, JsonObject object)
        {
            Smelting smelting = new Smelting();
            JsonElement smelting_element = Optional.ofNullable(object.get("smelting")).orElseGet(() -> new JsonObject());
            if(smelting_element.isJsonPrimitive() && smelting_element.getAsJsonPrimitive().isString())
            {
                //TODO Variables
            }
            else if(smelting_element.isJsonObject())
            {
                JsonObject smelting_object = smelting_element.getAsJsonObject();
                JsonElement xp_element = smelting_object.get("xp");
                if(xp_element == null)
                    smelting.count = 1;
                else if(xp_element.isJsonPrimitive())
                {
                    JsonPrimitive count_primitive = xp_element.getAsJsonPrimitive();
                    String count_string;
                    if(count_primitive.isString() && (count_string = count_primitive.getAsString()).startsWith("#"))
                        smelting.xp = VariableManager.warnIfAbsent(VariableManager.getNumberConstant(new ResourceLocation(count_string.substring(1))), 0, "The string at %s/smelting/xp references a non-existent constant (%s). Defaulting to 0.", registry_name, count_string).floatValue();
                    else if(count_primitive.isNumber())
                        smelting.xp = count_primitive.getAsFloat();
                    else
                        MetalExtras.LOGGER.warn("The element at %s/smelting/xp is expected to be a #constant, JsonPrimitive Number, or a JsonObject. Defaulting to 0.", registry_name);
                }
                else
                {
                    //TODO throw exception
                }
                JsonElement item_element = smelting_object.get("item");
                if(item_element == null)
                    smelting.item = Items.AIR;
                else if(item_element.isJsonPrimitive() && item_element.getAsJsonPrimitive().isString())
                {
                    String result_item_string = item_element.getAsString();
                    if(result_item_string.startsWith("#"))
                        smelting.item = VariableManager.warnIfAbsent(VariableManager.getConstant(new ResourceLocation(result_item_string.substring(1)), Item.class), Items.AIR, "The string at %s/smelting/item references a non-existent constant (%s)", registry_name, result_item_string);
                    else
                        smelting.item = VariableManager.warnIfAbsent(Optional.ofNullable(ForgeRegistries.ITEMS.getValue(new ResourceLocation(result_item_string))), Items.AIR, "The string at %s/smelting/item references an unregistered item (%s). Defaulting to air.", registry_name, result_item_string);
                }
                else
                {
                    //TODO throw exception
                }
                JsonElement count_element = smelting_object.get("count");
                if(count_element == null)
                    smelting.count = 1;
                else if(count_element.isJsonPrimitive())
                {
                    JsonPrimitive count_primitive = count_element.getAsJsonPrimitive();
                    String count_string;
                    if(count_primitive.isString() && (count_string = count_primitive.getAsString()).startsWith("#"))
                        smelting.count = VariableManager.warnIfAbsent(VariableManager.getNumberConstant(new ResourceLocation(count_string.substring(1))), 1, "The string at %s/smelting/count references a non-existent constant (%s)", registry_name, count_string).intValue();
                    else if(count_primitive.isNumber())
                        smelting.count = count_primitive.getAsInt();
                    else
                        MetalExtras.LOGGER.warn("The element at %s/smelting/count is expected to be a #constant, JsonPrimitive Number, or a JsonObject. Defaulting to 1.", registry_name);
                }
                else
                {
                    //TODO throw exception
                }
                JsonElement nbt_element = smelting_object.get("nbt");
                if(nbt_element == null)
                    smelting.nbt = new NBTTagCompound();
                else if(nbt_element.isJsonPrimitive() && nbt_element.getAsJsonPrimitive().isString())
                {
                    String nbt_string = nbt_element.getAsString();
                    if(nbt_string.startsWith("#"))
                        smelting.nbt = VariableManager.warnIfAbsent(VariableManager.getConstant(new ResourceLocation(nbt_string.substring(1)), NBTTagCompound.class), new NBTTagCompound(), "The string at %s/smelting/count references a non-existent constant (%s)", registry_name, nbt_string);
                    else
                    {
                        try
                        {
                            smelting.nbt = JsonToNBT.getTagFromJson(nbt_string);
                        }
                        catch(NBTException exception)
                        {
                            MetalExtras.LOGGER.warn("There was an exception while trying to convert the string at %s/smelting/nbt into an NBTTagCompound. Defaulting to an empty NBTTagCompound.", registry_name);
                            smelting.nbt = new NBTTagCompound();
                        }
                    }
                }
                else if(nbt_element.isJsonObject())
                    try
                    {
                        smelting.nbt = JsonToNBT.getTagFromJson(TypeAdapters.JSON_ELEMENT.toJson(nbt_element));
                    }
                    catch(NBTException exception)
                    {
                        MetalExtras.LOGGER.warn("There was an exception while trying to convert the string at %s/smelting/nbt into an NBTTagCompound. Defaulting to an empty NBTTagCompound.", registry_name);
                        smelting.nbt = new NBTTagCompound();
                    }
                else
                    MetalExtras.LOGGER.warn("The element at %s/smelting/nbt is expected to be a #constant, JsonPrimitive String, or a JsonObject. Defaulting to an empty NBTTagCompound.", registry_name);
            }
            else
            {
                //TODO throw exception
            }
            return smelting;
        }
        
        public static Model parseModel(ResourceLocation registry_name, JsonObject object)
        {
            Model model = new Model();
            JsonObject model_object = JsonUtils.getJsonObject(object, "model", new JsonObject());
            model.three_dimensional = JsonUtils.getBoolean(model_object, "3d", false);
            model.texture = new ResourceLocation(JsonUtils.getString(model_object, "texture", "missingno"));
            model.model_type = ModelType.byName(JsonUtils.getString(model_object, "model_type", "IRON"));
            return model;
        }
    }
    
    public static class Block
    {
        protected int harvest_level = 0;
        protected Drop[] drops = new Drop[0];
        protected int min_xp = 0;
        protected int max_xp = 0;
        protected CreativeTabs[] creative_tabs = new CreativeTabs[0];
        
        public final int getHarvestLevel()
        {
            return this.harvest_level;
        }
        
        public final Drop[] getDrops()
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
        
        public static class Drop
        {
            protected Item item = Items.AIR;
            protected int metadata = 0;
            protected NBTTagCompound nbt = new NBTTagCompound();
            protected float[] chances = new float[] { 1F } ;
            protected int[] min_counts = new int[] { 1 };
            protected int[] max_counts = { 1 };
            
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
    
    public static class Generation implements Predicate<IBlockState>
    {
        public final GenerateMinable.EventType event;
        public final boolean post_event;
        private NewOreType parent;
        protected int tries = 0;
        protected int min_height = 0;
        protected int max_height = 0;
        protected int size = 0;
        protected float min_temperature = -Float.MAX_VALUE;
        protected float max_temperature = Float.MAX_VALUE;
        protected BiMap<IBlockState, OreType> allowed_states = HashBiMap.create();
        protected WorldGenerator generator = new DefaultGenerator(this);
        
        public Generation(GenerateMinable.EventType event, boolean post_event)
        {
            this.event = event;
            this.post_event = event != null && post_event;
        }
        
        public final int getSpawnTries()
        {
            return this.tries;
        }
        
        public final int getMinHeight()
        {
            return this.min_height;
        }
        
        public final int getMaxHeight()
        {
            return this.max_height;
        }
        
        public final int getVeinSize()
        {
            return this.size;
        }
        
        public final boolean canGenerate()
        {
            return this.tries > 0 && this.max_height > 0 && this.max_height > this.min_height && this.size > 0;
        }
        
        public final float getMinTemperature()
        {
            return this.min_temperature;
        }
        
        public final float getMaxTemperature()
        {
            return this.max_temperature;
        }
        
        @Override
        public boolean apply(IBlockState state)
        {
            return this.allowed_states.containsKey(state);
        }
        
        public final WorldGenerator getGenerator()
        {
            return this.generator;
        }
        
        public NewOreType getParent()
        {
            return this.parent;
        }
    }
    
    public static class Smelting
    {
        protected float xp;
        protected Item item = Items.AIR;
        protected int metadata = 0;
        protected int count = 1;
        protected NBTTagCompound nbt;
        
        public float getXp()
        {
            return this.xp;
        }
        
        public Item getItem()
        {
            return this.item;
        }
        
        public int getMetadata()
        {
            return this.metadata;
        }
        
        public int getCount()
        {
            return this.count;
        }
        
        public NBTTagCompound getNbt()
        {
            return this.nbt;
        }
    }
    
    public static class Model
    {
        protected boolean three_dimensional = false;
        protected ResourceLocation texture = new ResourceLocation("missingno");
        protected ModelType model_type = ModelType.IRON;
        
        public final boolean get3d()
        {
            return false;
        }
        
        public final ResourceLocation getTexture()
        {
            return this.texture;
        }
        
        public final ModelType getModelType()
        {
            return this.model_type;
        }
    }
    
    public static class DefaultGenerator extends WorldGenerator
    {
        private final Generation generation;
        
        public DefaultGenerator(Generation generation)
        {
            this.generation = generation;
        }
        @Override
        
        public boolean generate(World world, Random random, BlockPos pos)
        {
            return OreUtils.generateOres(world, pos, random, random.nextInt(generation.getVeinSize()) + 1, generation.parent);
        }
        
    }
}
