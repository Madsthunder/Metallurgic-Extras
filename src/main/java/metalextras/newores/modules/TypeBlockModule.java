package metalextras.newores.modules;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.TypeAdapters;
import api.metalextras.BlockOre;
import api.metalextras.OreTypes;
import api.metalextras.OreUtils;
import metalextras.newores.NewOreType;
import metalextras.newores.VariableManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class TypeBlockModule extends OreModule<NewOreType, TypeBlockModule>
{
	protected Map<OreTypes, ResourceLocation> name_overrides = Maps.newHashMap();
	protected Function<WorldContext<TypeBlockModule, Void>, Integer> harvest_level_getter = ConstantFunction.of(0);
	protected TypeBlockModule.Drop[] drops = new TypeBlockModule.Drop[0];
	protected Function<WorldContext<TypeBlockModule, Integer>, Integer> xp_getter = ConstantFunction.of(0);
	protected CreativeTabs[] creative_tabs = new CreativeTabs[0];

	public TypeBlockModule(String path, JsonElement json, boolean parse)
	{
		super(path, NewOreType.class, TypeBlockModule.class, json);
		if(parse)
		{
			JsonObject block_object = json.getAsJsonObject();
			this.name_overrides = Maps.newHashMap(VariableManager.constant(this.module_type, Map.class, path, json, "name_overrides", (e) ->
			{
				Map<OreTypes, ResourceLocation> name_overrides = Maps.newHashMap();
				for(Entry<String, JsonElement> entry : e.getAsJsonObject().entrySet())
				{
					Optional.ofNullable(OreUtils.getTypeCollectionsRegistry().getValue(new ResourceLocation(VariableManager.getStringConst(this.module_type, path, e, entry.getKey(), entry.getKey())))).ifPresent((materials) -> name_overrides.put(materials, new ResourceLocation(entry.getValue().getAsString())));
				}
				return name_overrides;
			}, this.name_overrides, VariableManager.JSON_OBJECT_NAME));
			/**JsonObject overrides_object = JsonUtils.getJsonObject(block_object, "name_overrides", new JsonObject());
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
			}*/
			this.harvest_level_getter = VariableManager.getIntegerVar(this.module_type, Void.class, path, json, "harvest_level", this.harvest_level_getter);
			this.drops = Iterables.toArray(VariableManager.constant(this.module_type, List.class, path, json, "drops", (e) ->
			{
				JsonArray array = e.getAsJsonArray();
				List<Drop> drops = Lists.newArrayList();
				for(int i = 0; i < array.size(); i++)
					drops.add(VariableManager.constant(this.module_type, Drop.class, String.format("%s/drops[%s]", path, i), array.get(i), null, (e1) ->
					{
						JsonObject object = e1.getAsJsonObject();
						Drop drop = new Drop(this);
						drop.item_getter = VariableManager.getRegistryEntryVar(this.module_type, Random.class, ForgeRegistries.ITEMS, path, object, "item", drop.item_getter);
						drop.metadata_getter = VariableManager.getIntegerVar(this.parent_type, Void.class, path, object, "data", drop.metadata_getter);
						drop.nbt_getter = VariableManager.variable(this.module_type, Void.class, NBTTagCompound.class, path, object, "nbt", TypeBlockModule::getNBTGetterFromJson, drop.nbt_getter, VariableManager.JSON_OBJECT_NAME);
						return drop;
					}, new Drop(this), VariableManager.JSON_OBJECT_NAME));
				return drops;
			}, Collections.emptyList(), VariableManager.JSON_OBJECT_NAME), Drop.class);
			this.xp_getter = VariableManager.variable(this.module_type, Integer.class, Integer.class, path, json, "xp", (e) ->
			{
				if(e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber())
				{
					Function<WorldContext<TypeBlockModule, Integer>, Integer> max_xp_getter = ConstantFunction.of(e.getAsInt());
					return (context) ->
					{
						int max_xp = max_xp_getter.apply(context);
						if(max_xp <= 0)
							return 0;
						return (context.access instanceof World ? ((World)context.access).rand : BlockOre.getBlockRandom()).nextInt(max_xp + 1);
					};
				}
				else if(e.isJsonObject())
				{
					JsonObject xp_object = e.getAsJsonObject();
					Function<WorldContext<TypeBlockModule, Integer>, Integer> xp0_getter = VariableManager.getIntegerVar(this.module_type, Integer.class, path.concat("/xp"), e, "min", ConstantFunction.of(0));
					Function<WorldContext<TypeBlockModule, Integer>, Integer> xp1_getter = VariableManager.getIntegerVar(this.module_type, Integer.class, path.concat("/xp"), e, "max", ConstantFunction.of(0));
					return (context) ->
					{
						int xp0 = xp0_getter.apply(context);
						int xp1 = xp1_getter.apply(context);
						int min_xp = Math.min(xp0, xp1);
						int max_xp = Math.max(xp0, xp1);
						if(max_xp <= 0)
							return 0;
						return (context.access instanceof World ? ((World)context.access).rand : BlockOre.getBlockRandom()).nextInt(max_xp - min_xp + 1) + min_xp;
					};
				}
				else
					return null;
			}, this.xp_getter, VariableManager.JSON_NUMBER_NAME, VariableManager.JSON_OBJECT_NAME);
			JsonArray block_creative_tab_array = JsonUtils.getJsonArray(block_object, "creative_tabs", new JsonArray());
			List<CreativeTabs> block_creative_tab_list = Lists.newArrayList();
			for(int i = 0; i < block_creative_tab_array.size(); i++)
				block_creative_tab_list.addAll(Lists.newArrayList(OreUtils.getCreativeTabs(block_creative_tab_array.get(i).getAsString())));
			this.creative_tabs = Iterables.toArray(block_creative_tab_list, CreativeTabs.class);
		}
	}

	public final int getHarvestLevel(IBlockState state)
	{
		return this.harvest_level_getter.apply(WorldContext.of(this, state));
	}

	public final TypeBlockModule.Drop[] getDrops()
	{
		return this.drops;
	}

	public final int getXp(IBlockState state, IBlockAccess access, BlockPos pos, int fortune)
	{
		return this.xp_getter.apply(WorldContext.of(this, state, access, pos, null, fortune));
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
	
	public static Function<WorldContext<TypeBlockModule, Void>, NBTTagCompound> getNBTGetterFromJson(JsonElement element)
	{
		try
		{
			if(element.isJsonObject())
			{
				NBTTagCompound compound = new NBTTagCompound();
				NBTTagCompound nbt = JsonToNBT.getTagFromJson(TypeAdapters.JSON_ELEMENT.toJson(element.getAsJsonObject()));
				if(nbt.hasKey("ForgeCaps"))
				{
					compound.setTag("ForgeCaps", nbt.getCompoundTag("ForgeCaps"));
					nbt.removeTag("ForgeCaps");
				}
				compound.setTag("tag", nbt);
				return ConstantFunction.of(compound);
			}
		}
		catch(Exception e)
		{
			// TODO I wanna be a real error! (Yes, that's a pinnochio
			// reference)
			e.printStackTrace();
		}
		return null;
	}

	public static class Drop
	{
		private final TypeBlockModule parent;
		protected Function<WorldContext<TypeBlockModule, Random>, Item> item_getter = ConstantFunction.of(Items.AIR);
		protected Function<WorldContext<NewOreType, Void>, Integer> metadata_getter = ConstantFunction.of(0);
		protected Function<WorldContext<TypeBlockModule, Void>, NBTTagCompound> nbt_getter = ConstantFunction.of(new NBTTagCompound());
		protected Function<WorldContext<TypeBlockModule, Integer>, Float> chance_getter = ConstantFunction.of(1F);
		protected Function<WorldContext<TypeBlockModule, Integer>, Integer> min_count_getter = ConstantFunction.of(1);
		protected Function<WorldContext<TypeBlockModule, Integer>, Integer> max_count_getter = ConstantFunction.of(1);

		public Drop(TypeBlockModule parent)
		{
			this.parent = parent;
		}

		public final Item getItem(IBlockState state, Random random)
		{
			return this.item_getter.apply(WorldContext.of(this.parent, state, null, null, null, random));
		}

		public final int getMetadata(IBlockState state)
		{
			return this.metadata_getter.apply(WorldContext.of(this.parent.getParentModule(), state));
		}

		public final NBTTagCompound getNbt(IBlockState state)
		{
			return this.nbt_getter.apply(WorldContext.of(this.parent, state)).copy();
		}

		public final float getChance(IBlockState state, int fortune)
		{
			return this.chance_getter.apply(WorldContext.of(this.parent, state, null, null, null, fortune));
		}

		public final int getMinCount(IBlockState state, int fortune)
		{
			return this.min_count_getter.apply(WorldContext.of(this.parent, state, null, null, null, fortune));
		}

		public final int getMaxCount(IBlockState state, int fortune)
		{
			return this.min_count_getter.apply(WorldContext.of(this.parent, state, null, null, null, fortune));
		}
	}
}