package metalextras.newores;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import api.metalextras.BlockOre;
import api.metalextras.OreType;
import api.metalextras.OreTypes;
import api.metalextras.OreUtils;
import metalextras.newores.modules.BlockModule;
import metalextras.newores.modules.GenerationModule;
import metalextras.newores.modules.TypeModelModule;
import metalextras.newores.modules.OreModule;
import metalextras.newores.modules.SmeltingModule;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class NewOreType extends OreModule<NewOreType, NewOreType> implements IForgeRegistryEntry<NewOreType>
{
	public static final String DEFAULT_NAME = "tile.metalextras:unnamed";
	protected final Map<OreTypes, BlockOre> blocks = Maps.newHashMap();
	private ResourceLocation registry_name;
	protected BlockModule block;
	protected GenerationModule generation;
	protected String name = DEFAULT_NAME;
	protected CreativeTabs[] item_creative_tabs = new CreativeTabs[0];
	protected String[] ore_dictionary = new String[0];

	public NewOreType(String path, JsonObject json, boolean parse)
	{
		super(path, NewOreType.class, NewOreType.class, json);
		if(parse)
		{
			this.name = JsonUtils.getString(json, "name", DEFAULT_NAME);
			JsonArray oredict_array = JsonUtils.getJsonArray(json, "ore_dictionary", new JsonArray());
			List<String> oredict_list = Lists.newArrayList();
			for(int i = 0; i < oredict_array.size(); i++)
				oredict_list.add(oredict_array.get(i).getAsString());
			this.ore_dictionary = Iterables.toArray(oredict_list, String.class);
			JsonObject item = JsonUtils.getJsonObject(json, "item", new JsonObject());
			JsonArray item_creative_tab_array = JsonUtils.getJsonArray(item, "creative_tabs", new JsonArray());
			List<CreativeTabs> item_creative_tab_list = Lists.newArrayList();
			for(int i = 0; i < item_creative_tab_array.size(); i++)
				item_creative_tab_list.addAll(Lists.newArrayList(OreUtils.getCreativeTabs(item_creative_tab_array.get(i).getAsString())));
			this.item_creative_tabs = Iterables.toArray(item_creative_tab_list, CreativeTabs.class);
		}
	}

	public BlockModule getBlockModule()
	{
		return this.block;
	}

	public GenerationModule getGenerationModule()
	{
		return this.generation;
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

	@Override
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

	public BlockOre createBlock(OreTypes materials)
	{
		ModContainer previous_mod = Loader.instance().activeModContainer();
		Loader.instance().setActiveModContainer(Loader.instance().getIndexedModList().get(this.registry_name.getResourceDomain()));
		BlockOre ore = new BlockOre(this, materials, (type, types) -> this.block.hasNameOverride(types) ? this.block.getNameOverride(types) : BlockOre.getDefaultRegistryName(type, types));
		Loader.instance().setActiveModContainer(previous_mod);
		return ore;
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

	@Override
	public Map<Class<?>, OreModule<NewOreType, ?>> gatherDefaultChildren(String path, JsonObject json)
	{
		Map<Class<?>, OreModule<NewOreType, ?>> children = Maps.newHashMap();
		children.put(BlockModule.class, this.block = VariableManager.newModule(path, BlockModule.class, json));
		children.put(GenerationModule.class, this.generation = VariableManager.newModule(path, GenerationModule.class, json));
		children.put(SmeltingModule.class, VariableManager.newModule(path, SmeltingModule.class, json));
		children.put(TypeModelModule.class, VariableManager.newModule(path, TypeModelModule.class, json));
		return children;
	}

	@Override
	public final Class<NewOreType> getRegistryType()
	{
		return NewOreType.class;
	}

	public final NewOreType setRegistryName(String modid, String name)
	{
		return this.setRegistryName(new ResourceLocation(modid, name));
	}

	public final NewOreType setRegistryName(String name)
	{
		return this.setRegistryName(new ResourceLocation(String.format("%s%s", name.lastIndexOf(":") == -1 ? String.format("%s:", Loader.instance().activeModContainer().getModId()) : "", name)));
	}

	@Override
	public final NewOreType setRegistryName(ResourceLocation name)
	{
		if(this.registry_name != null)
			throw new IllegalStateException(String.format("Attempted to set registry name with existing registry name! New: %s Old: %s", name, this.registry_name));
		Optional<ModContainer> optional = Optional.ofNullable(Loader.instance().activeModContainer()).filter((mod) -> !(mod instanceof InjectedModContainer && ((InjectedModContainer)mod).wrappedContainer instanceof FMLContainer));
		String modid = optional.isPresent() ? optional.get().getModId() : "minecraft";
		if(!modid.equals(name.getResourceDomain()))
			FMLLog.bigWarning("Dangerous alternative prefix `%s` for name `%s`, expected `%s` invalid registry invocation/invalid name?", name.getResourceDomain(), name, modid);
		this.registry_name = name;
		return this;
	}

	@Override
	public final ResourceLocation getRegistryName()
	{
		return this.registry_name;
	}

	public static class DefaultGenerator extends WorldGenerator
	{
		private final GenerationModule.Properties properties;

		public DefaultGenerator(GenerationModule.Properties properties)
		{
			this.properties = properties;
		}

		@Override
		public boolean generate(World world, Random random, BlockPos pos)
		{
			return OreUtils.generateOres(world, pos, random, random.nextInt(this.properties.getVeinSize()) + 1, this.properties);
		}
	}
}
