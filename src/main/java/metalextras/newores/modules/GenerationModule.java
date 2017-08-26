package metalextras.newores.modules;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import api.metalextras.OreType;
import api.metalextras.OreUtils;
import metalextras.MetalExtras;
import metalextras.newores.FilterManager;
import metalextras.newores.NewOreType;
import metalextras.newores.NewOreType.DefaultGenerator;
import metalextras.newores.VariableManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;

public class GenerationModule extends OreModule<NewOreType, GenerationModule> implements Predicate<IBlockState>
{
	private NewOreType parent;
	protected boolean post_event;
	protected GenerateMinable.EventType event;
	protected Function<WorldContext<GenerationModule, Void>, Properties> properties_getter = ConstantFunction.of(new Properties.Iron(this, true));
	protected BiMap<IBlockState, OreType> allowed_states = HashBiMap.create();

	public GenerationModule(String path, JsonElement json, boolean parse)
	{
		super(path, NewOreType.class, GenerationModule.class, json);
		if(parse)
		{
			JsonObject generation_object = json.getAsJsonObject();
			JsonElement event_element = generation_object.has("event") ? generation_object.get("event") : new JsonObject();
			if(event_element.isJsonObject())
			{
				JsonObject event_object = JsonUtils.getJsonObject(generation_object, "event", new JsonObject());
				this.event = OreUtils.getEventType(JsonUtils.getString(event_object, "name", ""));
				this.post_event = JsonUtils.getBoolean(event_object, "post", true);
			}
			else if(event_element.isJsonPrimitive() && event_element.getAsJsonPrimitive().isString())
			{
				this.event = OreUtils.getEventType(event_element.getAsString());
				this.post_event = true;
			}
			else
			{
				MetalExtras.LOGGER.warn(String.format("The element at %s/event is expected to be a JsonPrimitive String or a JsonObject.", path));
				this.event = null;
				this.post_event = false;
			}
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
							MetalExtras.LOGGER.warn(String.format("The element at %s/filters[%s]/type references a non-existent filter (%s)", path, i, type_string));
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
								MetalExtras.LOGGER.warn(String.format("The element at %s/filters[%s]/params[%s] must be a string", path, i, j));
						}
						filter.accept(materials, param_set);
					}
					else
						MetalExtras.LOGGER.warn(String.format("The element at %s/filters[%s] must be a JsonObject", path, i));
				}
			}
			for(OreType material : materials)
				this.allowed_states.put(material.getState(), material);
			Function<Object, Object> f = (o) -> null;
			this.properties_getter = VariableManager.variableOrParse(GenerationModule.class, GenerationModule.class, Void.class, Properties.class, path, generation_object.get("properties"), (properties_object) -> JsonUtils.getString(properties_object, "type", "minecraft:iron"), this, this.properties_getter);
		}
	}

	public final boolean shouldPostEvent()
	{
		return this.event != null && this.post_event;
	}

	public final boolean hasEvent()
	{
		return this.event != null;
	}

	public final GenerateMinable.EventType getEvent()
	{
		return this.event;
	}

	public final GenerationModule.Properties getProperties(World world, BlockPos pos)
	{
		return this.properties_getter.apply(WorldContext.of(this, null, world, pos, null, null));
	}

	@Override
	public final boolean apply(IBlockState state)
	{
		return this.allowed_states.containsKey(state);
	}

	public abstract static class Properties
	{
		private final GenerationModule parent;
		protected int size = 0;
		protected float min_temperature = -Float.MAX_VALUE;
		protected float max_temperature = Float.MAX_VALUE;
		protected WorldGenerator generator = new DefaultGenerator(this);

		public Properties(GenerationModule parent, boolean init)
		{
			this.parent = parent;
			if(init)
				this.init();
		}

		public void init()
		{
		}

		public abstract int randomSpawnTries(Random random);

		public abstract int randomHeight(Random random);

		public final int getVeinSize()
		{
			return this.size;
		}

		public boolean canGenerate()
		{
			return this.size > 0;
		}

		public final float getMinTemperature()
		{
			return this.min_temperature;
		}

		public final float getMaxTemperature()
		{
			return this.max_temperature;
		}

		public final WorldGenerator getGenerator()
		{
			return this.generator;
		}

		public final GenerationModule getParent()
		{
			return this.parent;
		}

		public static class Iron extends GenerationModule.Properties
		{
			protected int tries = 0;
			protected int min_height = 0;
			protected int max_height = 0;

			public Iron(GenerationModule generation, boolean init)
			{
				super(generation, false);
				if(init)
					this.init();
			}

			@Override
			public int randomSpawnTries(Random random)
			{
				return this.tries;
			}

			@Override
			public int randomHeight(Random random)
			{
				return random.nextInt(this.max_height - this.min_height) + this.min_height;
			}

			@Override
			public boolean canGenerate()
			{
				return super.canGenerate() && this.tries > 0 && this.max_height > 0 && this.max_height > this.min_height;
			}

			public static BiFunction<World, BlockPos, GenerationModule.Properties> createDefaultGetter(GenerationModule generation)
			{
				Properties.Iron properties = new Iron(generation, true);
				return (world, pos) -> properties;
			}

			public static BiFunction<JsonObject, GenerationModule, Properties> createParser()
			{
				return (json, generation) ->
				{
					GenerationModule.Properties.Iron properties = new GenerationModule.Properties.Iron(generation, true);
					properties.tries = JsonUtils.getInt(json, "tries", 0);
					JsonElement height = json.get("height");
					if(height.isJsonObject())
					{
						JsonObject height_object = height.getAsJsonObject();
						properties.min_height = JsonUtils.getInt(height_object, "min", 0);
						properties.max_height = JsonUtils.getInt(height_object, "max", 0);
					}
					else
						properties.max_height = height.getAsInt();
					properties.size = JsonUtils.getInt(json, "size", 1);
					JsonObject temperature_object = JsonUtils.getJsonObject(json, "temperature", new JsonObject());
					properties.min_temperature = JsonUtils.getFloat(temperature_object, "min", -Float.MAX_VALUE);
					properties.max_temperature = JsonUtils.getFloat(temperature_object, "max", Float.MAX_VALUE);
					return properties;
				};
			}
		}

		public static class Lapis extends GenerationModule.Properties
		{
			protected int tries = 0;
			protected int center_height = 0;
			protected int spread = 1;

			public Lapis(GenerationModule generation, boolean init)
			{
				super(generation, false);
				if(init)
					this.init();
			}

			@Override
			public int randomSpawnTries(Random random)
			{
				return this.tries;
			}

			@Override
			public int randomHeight(Random random)
			{
				return random.nextInt(this.spread) + random.nextInt(this.spread) + this.center_height - this.spread;
			}

			@Override
			public boolean canGenerate()
			{
				return super.canGenerate() && this.tries > 0 && this.spread > 0 && this.center_height + this.spread - 2 >= 0 && this.center_height - this.spread < 256;
			}

			public static BiFunction<World, BlockPos, GenerationModule.Properties> createDefaultGetter(GenerationModule generation)
			{
				Properties.Lapis properties = new Lapis(generation, true);
				return (world, pos) -> properties;
			}

			public static BiFunction<JsonObject, GenerationModule, Properties> createParser()
			{
				return (json, generation) ->
				{
					GenerationModule.Properties.Lapis properties = new GenerationModule.Properties.Lapis(generation, true);
					properties.tries = JsonUtils.getInt(json, "tries", 0);
					properties.center_height = JsonUtils.getInt(json, "center_height", 0);
					properties.spread = JsonUtils.getInt(json, "spread", 1);
					properties.size = JsonUtils.getInt(json, "size", 1);
					JsonObject temperature_object = JsonUtils.getJsonObject(json, "temperature", new JsonObject());
					properties.min_temperature = JsonUtils.getFloat(temperature_object, "min", -Float.MAX_VALUE);
					properties.max_temperature = JsonUtils.getFloat(temperature_object, "max", Float.MAX_VALUE);
					return properties;
				};
			}
		}

		public static class Emerald extends GenerationModule.Properties
		{
			protected int tries_base = 0;
			protected int tries_randomizer = 1;
			protected int min_height = 0;
			protected int max_height = 0;

			public Emerald(GenerationModule generation, boolean init)
			{
				super(generation, false);
				if(init)
					this.init();
			}

			@Override
			public int randomSpawnTries(Random random)
			{
				return this.tries_base + random.nextInt(this.tries_randomizer);
			}

			@Override
			public int randomHeight(Random random)
			{
				return random.nextInt(this.max_height - this.min_height) + this.max_height;
			}

			@Override
			public boolean canGenerate()
			{
				return super.canGenerate() && this.tries_base + this.tries_randomizer - 1 > 0 && this.max_height > 0 && this.max_height > this.min_height;
			}

			public static BiFunction<World, BlockPos, GenerationModule.Properties> createDefaultGetter(GenerationModule generation)
			{
				Properties.Emerald properties = new Emerald(generation, true);
				return (world, pos) -> properties;
			}

			public static BiFunction<JsonObject, GenerationModule, Properties> createParser()
			{
				return (json, generation) ->
				{
					GenerationModule.Properties.Emerald properties = new GenerationModule.Properties.Emerald(generation, true);
					properties.tries_base = JsonUtils.getInt(json, "tries_base", 0);
					properties.tries_randomizer = JsonUtils.getInt(json, "tries_randomizer", 1);
					JsonElement height = json.get("height");
					if(height.isJsonObject())
					{
						JsonObject height_object = height.getAsJsonObject();
						properties.min_height = JsonUtils.getInt(height_object, "min", 0);
						properties.max_height = JsonUtils.getInt(height_object, "max", 0);
					}
					else
						properties.max_height = height.getAsInt();
					properties.size = JsonUtils.getInt(json, "size", 1);
					JsonObject temperature_object = JsonUtils.getJsonObject(json, "temperature", new JsonObject());
					properties.min_temperature = JsonUtils.getFloat(temperature_object, "min", -Float.MAX_VALUE);
					properties.max_temperature = JsonUtils.getFloat(temperature_object, "max", Float.MAX_VALUE);
					return properties;
			};
			}
		}

		public abstract static class GenerationPropertiesParser
		{
			public abstract GenerationModule.Properties parse(String path, GenerationModule generation, JsonObject properties_object);
		}
		
		public static interface PropertiesGetter
		{
			
		}
	}
}