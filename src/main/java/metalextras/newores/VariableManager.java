package metalextras.newores;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import metalextras.MetalExtras;
import metalextras.newores.modules.ConstantFunction;
import metalextras.newores.modules.GenerationModule;
import metalextras.newores.modules.GenerationModule.Properties.GenerationPropertiesParser;
import metalextras.newores.modules.OreModule;
import metalextras.newores.modules.WorldContext;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class VariableManager
{
	public static final String JSON_BOOLEAN_NAME = "JsonPrimitive Boolean";
	public static final String JSON_NUMBER_NAME = "JsonPrimitive Number";
	public static final String JSON_STRING_NAME = "JsonPrimitive String";
	public static final String JSON_ARRAY_NAME = "JsonArray";
	public static final String JSON_OBJECT_NAME = "JsonObject";
	
	private static final Supplier<Object> EMPTY = () -> null;
	private static final Map<ResourceLocation, List<Constant<?, ?>>> CONSTANTS = Maps.newHashMap();
	private static final Map<ResourceLocation, List<Parser<?, ?, ?>>> PARSERS = Maps.newHashMap();
	private static final Map<ResourceLocation, List<Variable<?, ?, ?>>> VARIABLES = Maps.newHashMap();
	private static final Map<ResourceLocation, Function<GenerationModule, BiFunction<World, BlockPos, GenerationModule.Properties>>> GENERATION_PROPERTIES = Maps.newHashMap();
	private static final Map<ResourceLocation, GenerationPropertiesParser> PROPERTIES_PARSERS = Maps.newHashMap();
	private static final Map<Class<?>, String> MASTER_MODULES = Maps.newHashMap();
	private static final Map<Class<?>, BiFunction<ResourceLocation, JsonElement, ? extends OreModule<?, ?>>> MASTER_MODULE_FACTORIES = Maps.newHashMap();
	private static final Map<Class<?>, String> MODULES = Maps.newHashMap();
	private static final Map<Class<?>, BiFunction<String, JsonElement, ? extends OreModule<?, ?>>> MODULE_FACTORIES = Maps.newHashMap();

	public static <M extends OreModule<?, M >, V> void registerConst(ResourceLocation name, Class<M> module_type, Class<V> return_type, V constant)
	{
		CONSTANTS.computeIfAbsent(name, (n) -> Lists.newArrayList()).add(Constant.of(module_type, return_type, constant));
	}

	public static <M extends OreModule<?, M >, V> void registerConst(ResourceLocation name, Predicate<Class<?>> module_type_predicate, Class<V> return_type, V constant)
	{
		CONSTANTS.computeIfAbsent(name, (n) -> Lists.newArrayList()).add(Constant.of(module_type_predicate, return_type, constant));
	}

	public static <M extends OreModule<?, M>, V> V getConst(ResourceLocation name, Class<M> module_type, Class<? super V> return_type)
	{
		return (V)Optional.ofNullable(CONSTANTS.get(name)).map((consts) -> Iterables.find(Lists.reverse(consts), (constt) -> constt.isAssignableFrom(module_type, return_type), null)).map((constant) -> constant.constant).orElse(null);
	}

	public static <M extends OreModule<?, M >, E, V> void registerPar(ResourceLocation name, Class<M> module_type, Class<E> context_type, Class<V> return_type, BiFunction<JsonObject, E, V> constant)
	{
		PARSERS.computeIfAbsent(name, (n) -> Lists.newArrayList()).add(Parser.of(module_type, context_type, return_type, constant));
	}

	public static <M extends OreModule<?, M >, E, V> void registerPar(ResourceLocation name, ContextualPredicate<M, E, V> type_tester, BiFunction<JsonObject, E, V> parser)
	{
		PARSERS.computeIfAbsent(name, (n) -> Lists.newArrayList()).add(Parser.of(type_tester, parser));
	}

	public static <M extends OreModule<?, M>, E, V> BiFunction<JsonObject, E, V> getPar(ResourceLocation name, Class<M> module_type, Class<E> context_type, Class<? super V> return_type)
	{
		return (BiFunction<JsonObject, E, V>)Optional.ofNullable(PARSERS.get(name)).map((pars) -> Iterables.find(Lists.reverse(pars), (par) -> par.isAssignableFrom(module_type, context_type, return_type), null)).map(parser -> parser.parser).orElse(null);
	}
	
	public static <M extends OreModule<?, M>, E, V, W extends WorldContext<M, E>> void registerVar(ResourceLocation name, Class<M> module_type, Class<E> context_type, Class<V> return_type, Function<WorldContext<M, E>, V> getter)
	{
		VARIABLES.computeIfAbsent(name, (n) -> Lists.newArrayList()).add(Variable.of(module_type, context_type, return_type, getter));
	}

	public static <M extends OreModule<?, M>, E, V, W extends WorldContext<M, E>> void registerVar(ResourceLocation name, ContextualPredicate<M, E, V> type_tester, Function<WorldContext<M, E>, V> getter)
	{
		VARIABLES.computeIfAbsent(name, (n) -> Lists.newArrayList()).add(Variable.of(type_tester, getter));
	}

	public static <M extends OreModule<?, M>, E, V> Function<WorldContext<M, E>, V> getVar(ResourceLocation name, Class<M> module_type, Class<E> context_type, Class<V> return_type)
	{
		return (Function<WorldContext<M, E>, V>)Optional.ofNullable(VARIABLES.get(name)).<Function<?, ?>>map((vars) -> Iterables.find(Lists.reverse(vars), (var) -> var.isAssignableFrom(module_type, context_type, return_type), null)).orElseGet(() -> Optional.ofNullable(getConst(name, module_type, return_type)).map(ConstantFunction::of).orElse(null));
	}

	public static void registerConstantGenerationProperties(ResourceLocation name, Function<GenerationModule, BiFunction<World, BlockPos, GenerationModule.Properties>> getter)
	{
		GENERATION_PROPERTIES.put(name, getter);
	}

	public static Optional<BiFunction<World, BlockPos, GenerationModule.Properties>> getConstantGenerationProperties(ResourceLocation name, GenerationModule generation)
	{
		return Optional.ofNullable(GENERATION_PROPERTIES.getOrDefault(name, (generation1) -> null).apply(generation));
	}

	public static void registerGenerationPropertiesParser(ResourceLocation name, GenerationPropertiesParser parser)
	{
		PROPERTIES_PARSERS.put(name, parser);
	}

	public static Optional<GenerationPropertiesParser> getGenerationPropertiesParser(ResourceLocation name)
	{
		return Optional.ofNullable(PROPERTIES_PARSERS.get(name));
	}

	public static <V> V warnIfAbsent(Optional<V> optional, V defaultt, String format, Object... params)
	{
		if(optional.isPresent())
			return optional.get();
		MetalExtras.LOGGER.warn(String.format(format, params));
		return defaultt;
	}

	public static <S extends OreModule<S, S>> void registerMasterModuleFactory(Class<S> module_type, String module_name, BiFunction<ResourceLocation, JsonElement, S> module_factory)
	{
		MASTER_MODULES.put(module_type, module_name);
		MASTER_MODULE_FACTORIES.put(module_type, module_factory);
	}

	public static <S extends OreModule<?, S>> void registerModuleFactory(Class<S> module_type, String module_name, BiFunction<String, JsonElement, S> module_factory)
	{
		MODULES.put(module_type, module_name);
		MODULE_FACTORIES.put(module_type, module_factory);
	}

	public static OreModule<?, ?> newMasterModule(ResourceLocation name, Class<?> module_type, JsonElement json)
	{
		return MASTER_MODULE_FACTORIES.get(module_type).apply(name, json.getAsJsonObject());
	}

	public static <S extends OreModule<?, S>> S newModule(String path, Class<S> module_type, JsonObject json)
	{
		return (S)MODULE_FACTORIES.get(module_type).apply(String.format("%s/%s", path, MODULES.get(module_type)), Optional.ofNullable(json.get(MODULES.get(module_type))).orElseGet(VariableManager::newJsonObject));
	}

	public static <S extends OreModule<?, S>> String getModuleName(Class<S> module_type)
	{
		return MODULES.get(module_type);
	}

	public static Stream<Entry<Class<?>, String>> masterModuleStream()
	{
		return MASTER_MODULES.entrySet().stream();
	}
	
	public static <M extends OreModule<?, M>, V> V constant(Class<M> module_type, Class<V> return_type, String path, JsonElement json, String element_name, Function<JsonElement, V> json_getter, V defaultt, String... valid_json_names)
	{
		JsonElement element = json;
		if(element_name != null)
		{
			if(json == null)
			{
				//TODO log exception
				return defaultt;
			}
			else if(json.isJsonObject())
				element = json.getAsJsonObject().get(element_name);
			else
			{
				//TODO log exception
				return defaultt;
			}
		}
		if(element != null)
		{
			JsonPrimitive primitive;
			String string;
			if(element.isJsonPrimitive() && (primitive = element.getAsJsonPrimitive()).isString() && (string = element.getAsString()).startsWith("#"))
			{
				V constant = getConst(new ResourceLocation(string.substring(1)), module_type, return_type);
				if(constant != null)
					return constant;
				MetalExtras.LOGGER.warn(String.format("The #constant at %s/%s (%s) is either not registered or is not applicable for the module type %s and the return type %s. Switching to default."), path, element_name, string, module_type.getName(), return_type.getName());
				return defaultt;
			}
			else if(json_getter != null)
			{
				V constant = json_getter.apply(element);
				if(constant != null)
					return constant;
			}
			MetalExtras.LOGGER.warn(String.format("The element at %s/%s is expected to be a %s%s. Switching to default.", path, element_name, Joiner.on(", ").join(valid_json_names), String.format("%s#constant", valid_json_names.length > 0 ? ", or a " : "")));
		}
		return defaultt;
	}
	
	public static <M extends OreModule<?, M>, E, V> V constantOrParse(Class<M> module_type, Class<E> context_type, Class<V> return_type, String path, JsonElement json, Function<JsonObject, String> parser_name_getter, E context, V defaultt)
	{
		return constant(module_type, return_type, path, json, null, (element) -> parse(module_type, context_type, return_type, path, element, parser_name_getter, context, defaultt), defaultt, JSON_OBJECT_NAME);
	}
	
	public static <M extends OreModule<?, M>, E, V> V parse(Class<M> module_type, Class<E> context_type, Class<V> return_type, String path, JsonElement json, Function<JsonObject, String> parser_name_getter, E context, V defaultt)
	{
		if(json != null)
		{
			if(json.isJsonObject())
			{
				JsonObject object = json.getAsJsonObject();
				String parser_name = parser_name_getter.apply(object);
				BiFunction<JsonObject, E, V> parser = getPar(new ResourceLocation(parser_name), module_type, context_type, return_type);
				if(parser != null)
					return Optional.ofNullable(parser.apply(object, context)).orElse(defaultt);
				MetalExtras.LOGGER.warn(String.format("The JsonObject at %s refers to a #parser that is either not registered or is not applicable for the module type %s, the context type %s, and the return type %s (%s). Switching to default.", path, module_type.getName(), context_type.getName(), return_type.getName(), parser_name));
			}
			else
				MetalExtras.LOGGER.warn(String.format("The element at %s is expected to be a JsonObject or a #constant. Switching to default."), path);
		}
		return defaultt;
	}
	
	public static <M extends OreModule<?, M>, E, V> Function<WorldContext<M, E>, V> variable(Class<M> module_type, Class<E> context_type, Class<V> return_type, String path, JsonElement json, String element_name, Function<JsonElement, Function<WorldContext<M, E>, V>> json_getter, Function<WorldContext<M, E>, ? extends V> defaultt, String... valid_json_names)
	{
		JsonElement element = json;
		if(element_name != null)
		{
			if(json == null)
			{
				//TODO log exception
			}
			else if(json.isJsonObject())
				element = json.getAsJsonObject().get(element_name);
			else
			{
				//TODO log exception
			}
		}
		if(element != null)
		{
			JsonPrimitive primitive;
			String string;
			if(element.isJsonPrimitive() && (primitive = element.getAsJsonPrimitive()).isString() && (string = element.getAsString()).startsWith("#"))
			{
				Function<WorldContext<M, E>, V> getter = getVar(new ResourceLocation(string.substring(1)), module_type, context_type, return_type);
				if(getter != null)
					return getter;
				MetalExtras.LOGGER.warn(String.format("The #variable at %s/%s (%s) is either not registered or is not applicable for the module type %s, the context type %s, and the return type %s. Switching to default.", path, element_name, string, module_type.getName(), context_type.getName(), return_type.getName()));
				return (Function<WorldContext<M, E>, V>)defaultt;
			}
			else if(json_getter != null)
			{
				Function<WorldContext<M, E>, V> from_json = json_getter.apply(element);
				if(from_json != null)
					return from_json;
			}
			MetalExtras.LOGGER.warn(String.format("The element at %s/%s is expected to be a %s%s. Switching to default."), path, element_name, Joiner.on(", ").join(valid_json_names), String.format("%s#variable", valid_json_names.length > 0 ? ", or a " : ""));
		}
		return (Function<WorldContext<M, E>, V>)defaultt;
	}
	
	public static <M extends OreModule<?, M>, PE, VE, V> Function<WorldContext<M, VE>, V> variableOrParse(Class<M> module_type, Class<PE> parser_context_type, Class<VE> variable_context_type, Class<V> return_type, String path, JsonElement json, Function<JsonObject, String> parser_name_getter, PE context, Function<WorldContext<M, VE>, V> defaultt)
	{
		return variable(module_type, variable_context_type, return_type, path, json, null, (element) -> Optional.<V>ofNullable(parse(module_type, parser_context_type, return_type, path, json, parser_name_getter, context, null)).map(ConstantFunction::<WorldContext<M, VE>, V>of).orElse(defaultt), defaultt, JSON_OBJECT_NAME);
	}
	
	public static <M extends OreModule<?, M>> boolean getBooleanConst(Class<M> module_type, String path, JsonElement json, String element_name, boolean defaultt)
	{
		return constant(module_type, Boolean.class, path, json, element_name, VariableManager::getBooleanFromElement, defaultt, JSON_BOOLEAN_NAME);
	}
	
	public static <M extends OreModule<?, M>, E> Predicate<WorldContext<M, E>> getBooleanVar(Class<M> module_type, Class<E> context_type, String path, JsonElement json, String element_name, Predicate<WorldContext<M, E>> defaultt)
	{
		Function<WorldContext<M, E>, Boolean> var = variable(module_type, context_type, Boolean.class, path, json, element_name, (element) -> ConstantFunction.of(VariableManager.getBooleanFromElement(element)), (context) -> defaultt.test(context), JSON_BOOLEAN_NAME);
		return (context) -> var.apply(context);
	}
	
	public static boolean getBooleanFromElement(JsonElement element)
	{
		return Optional.of(element).filter((e) -> e.isJsonPrimitive() && e.getAsJsonPrimitive().isBoolean()).map((e) -> e.getAsBoolean()).get();
	}
	
	public static <M extends OreModule<?, M>, E> Function<WorldContext<M, E>, Number> getNumberVar(Class<M> module_type, Class<E> context_type, String path, JsonElement json, String element_name, Function<WorldContext<M, E>, ? extends Number> defaultt)
	{
		return variable(module_type, context_type, Number.class, path, json, element_name, (element) -> ConstantFunction.of(VariableManager.getNumberFromElement(element)), defaultt, JSON_NUMBER_NAME);
	}
	
	public static <M extends OreModule<?, M>> Number getNumberConst(Class<M> module_type, String path, JsonElement json, String element_name, Number defaultt)
	{
		return constant(module_type, Number.class, path, json, element_name, VariableManager::getNumberFromElement, defaultt, JSON_NUMBER_NAME);
	}
	
	public static Number getNumberFromElement(JsonElement element)
	{
		return Optional.of(element).filter((e) -> e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()).map((e) -> e.getAsNumber()).get();
	}
	
	public static <M extends OreModule<?, M>> byte getByteConst(Class<M> module_type, String path, JsonElement json, String element_name, byte defaultt)
	{
		return getNumberConst(module_type, path, json, element_name, defaultt).byteValue();
	}
	
	public static <M extends OreModule<?, M>, E> Function<WorldContext<M, E>, Byte> getByteVar(Class<M> module_type, Class<E> context_type, String path, JsonElement json, String element_name, Function<WorldContext<M, E>, Byte> defaultt)
	{
		Function<WorldContext<M, E>, ? extends Number> function = getNumberVar(module_type, context_type, path, json, element_name, defaultt);
		if(function == defaultt)
			return defaultt;
		return (context) -> function.apply(context).byteValue();
	}
	
	public static <M extends OreModule<?, M>> short getShortConst(Class<M> module_type, String path, JsonElement json, String element_name, short defaultt)
	{
		return getNumberConst(module_type, path, json, element_name, defaultt).shortValue();
	}
	
	public static <M extends OreModule<?, M>, E> Function<WorldContext<M, E>, Short> getShortVar(Class<M> module_type, Class<E> context_type, String path, JsonElement json, String element_name, Function<WorldContext<M, E>, Short> defaultt)
	{
		Function<WorldContext<M, E>, ? extends Number> function = getNumberVar(module_type, context_type, path, json, element_name, defaultt);
		if(function == defaultt)
			return defaultt;
		return (context) -> function.apply(context).shortValue();
	}
	
	public static <M extends OreModule<?, M>> int getIntegerConst(Class<M> module_type, String path, JsonElement json, String element_name, int defaultt)
	{
		return getNumberConst(module_type, path, json, element_name, defaultt).intValue();
	}
	
	public static <M extends OreModule<?, M>, E> Function<WorldContext<M, E>, Integer> getIntegerVar(Class<M> module_type, Class<E> context_type, String path, JsonElement json, String element_name, Function<WorldContext<M, E>, Integer> defaultt)
	{
		Function<WorldContext<M, E>, ? extends Number> function = getNumberVar(module_type, context_type, path, json, element_name, defaultt);
		if(function == defaultt)
			return defaultt;
		return (context) -> function.apply(context).intValue();
	}
	
	public static <M extends OreModule<?, M>> float getFloatConst(Class<M> module_type, String path, JsonElement json, String element_name, float defaultt)
	{
		return getNumberConst(module_type, path, json, element_name, defaultt).floatValue();
	}
	
	public static <M extends OreModule<?, M>, E> Function<WorldContext<M, E>, Float> getFloatVar(Class<M> module_type, Class<E> context_type, String path, JsonElement json, String element_name, Function<WorldContext<M, E>, Float> defaultt)
	{
		Function<WorldContext<M, E>, ? extends Number> function = getNumberVar(module_type, context_type, path, json, element_name, defaultt);
		if(function == defaultt)
			return defaultt;
		return (context) -> function.apply(context).floatValue();
	}
	
	public static <M extends OreModule<?, M>> double getDoubleConst(Class<M> module_type, String path, JsonElement json, String element_name, double defaultt)
	{
		return getNumberConst(module_type, path, json, element_name, defaultt).doubleValue();
	}
	
	public static <M extends OreModule<?, M>, E> Function<WorldContext<M, E>, Double> getDoubleVar(Class<M> module_type, Class<E> context_type, String path, JsonElement json, String element_name, Function<WorldContext<M, E>, Double> defaultt)
	{
		Function<WorldContext<M, E>, ? extends Number> function = getNumberVar(module_type, context_type, path, json, element_name, defaultt);
		if(function == defaultt)
			return defaultt;
		return (context) -> function.apply(context).doubleValue();
	}
	
	public static <M extends OreModule<?, M>> long getLongConst(Class<M> module_type, String path, JsonElement json, String element_name, long defaultt)
	{
		return getNumberConst(module_type, path, json, element_name, defaultt).longValue();
	}
	
	public static <M extends OreModule<?, M>, E> Function<WorldContext<M, E>, Long> getLongVar(Class<M> module_type, Class<E> context_type, String path, JsonElement json, String element_name, Function<WorldContext<M, E>, Long> defaultt)
	{
		Function<WorldContext<M, E>, ? extends Number> function = getNumberVar(module_type, context_type, path, json, element_name, defaultt);
		if(function == defaultt)
			return defaultt;
		return (context) -> function.apply(context).longValue();
	}
	
	public static <M extends OreModule<?, M>> String getStringConst(Class<M> module_type, String path, JsonElement json, String element_name, String defaultt)
	{
		return constant(module_type, String.class, path, json, element_name, VariableManager::getStringFromElement, defaultt, JSON_STRING_NAME);
	}
	
	public static <M extends OreModule<?, M>, E> Function<WorldContext<M, E>, String> getStringVar(Class<M> module_type, Class<E> context_type, String path, JsonElement json, String element_name, Function<WorldContext<M, E>, String> defaultt)
	{
		return variable(module_type, context_type, String.class, path, json, element_name, (element) -> ConstantFunction.of(VariableManager.getStringFromElement(element)), defaultt, JSON_STRING_NAME);
	}
	
	public static <M extends OreModule<?, M>, V extends IForgeRegistryEntry<V>> V getRegistryEntryConst(Class<M> module_type, IForgeRegistry<V> registry, String path, JsonElement json, String element_name, V defaultt)
	{
		return constant(module_type, registry.getRegistrySuperType(), path, json, element_name, (element) -> getRegistryEntryFromElement(element, registry), defaultt, JSON_STRING_NAME);
	}
	
	public static <M extends OreModule<?, M>, E, V extends IForgeRegistryEntry<V>> Function<WorldContext<M, E>, V> getRegistryEntryVar(Class<M> module_type, Class<E> context_type, IForgeRegistry<V> registry, String path, JsonElement json, String element_name, Function<WorldContext<M, E>, V> defaultt)
	{
		return variable(module_type, context_type, registry.getRegistrySuperType(), path, json, element_name, (element) -> ConstantFunction.of(getRegistryEntryFromElement(element, registry)), defaultt, JSON_STRING_NAME);
	}
	
	public static String getStringFromElement(JsonElement element)
	{
		return Optional.of(element).filter((e) -> element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()).map((e) -> e.getAsString()).get();
	}
	
	public static <V extends IForgeRegistryEntry<V>> V getRegistryEntryFromElement(JsonElement element, IForgeRegistry<V> registry)
	{
		return Optional.ofNullable(getStringFromElement(element)).map((e) -> registry.getValue(new ResourceLocation(e))).get();
	}

	public static JsonObject newJsonObject()
	{
		return new JsonObject();
	}
	
	public static final class Constant<M extends OreModule<?, M>, V>
	{
		public final Predicate<Class<?>> module_type_predicate;
		public final Class<V> return_type;
		public final V constant;
		
		public Constant(Predicate<Class<?>> module_type_predicate, Class<V> return_type, V constant)
		{
			this.module_type_predicate = module_type_predicate;
			this.return_type = return_type;
			this.constant = constant;
		}
		
		public boolean isAssignableFrom(Class<?> module_type, Class<?> return_type)
		{
			return this.module_type_predicate.test(module_type) && return_type.isAssignableFrom(this.return_type);
		}
		
		public static <M extends OreModule<?, M>, V> Constant<M, V> of(Class<M> module_type, Class<V> return_type, V constant)
		{
			return of((other_module_type) -> other_module_type.isAssignableFrom(module_type), return_type, constant);
		}
		
		public static <M extends OreModule<?, M>, V> Constant<M, V> of(Predicate<Class<?>> module_type_predicate, Class<V> return_type, V constant)
		{
			return new Constant<M, V>(module_type_predicate, return_type, constant);
		}
	}
	
	public static final class Parser<M extends OreModule<?, M>, E, V>
	{
		public final ContextualPredicate<M, E, V> type_tester;
		public final BiFunction<JsonObject, E, V> parser;
		
		public Parser(ContextualPredicate<M, E, V> type_predicate, BiFunction<JsonObject, E, V> parser)
		{
			this.type_tester = type_predicate;
			this.parser = parser;
		}
		
		public boolean isAssignableFrom(Class<?> module_type, Class<?> context_type, Class<?> return_type)
		{
			return this.type_tester.test(module_type, context_type, return_type);
		}
		
		public static <M extends OreModule<?, M>, E, V> Parser<M, E, V> of(Class<M> module_type, Class<E> context_type, Class<V> return_type, BiFunction<JsonObject, E, V> parser)
		{
			return of(ContextualPredicate.of(module_type, context_type, return_type), parser);
		}
		
		public static <M extends OreModule<?, M>, E, V> Parser<M, E, V> of(ContextualPredicate<M, E, V> type_tester, BiFunction<JsonObject, E, V> parser)
		{
			return new Parser<M, E, V>(type_tester, parser);
		}
	}
	
	public static final class Variable<M extends OreModule<?, M>, E, V> implements Function<WorldContext<M, E>, V>
	{
		public final ContextualPredicate<M, E, V> type_tester;
		public final Function<WorldContext<M, E>, V> getter;
		
		public Variable(ContextualPredicate<M, E, V> type_tester, Function<WorldContext<M, E>, V> getter)
		{
			this.type_tester = type_tester;
			this.getter = getter;
		}
		
		public boolean isAssignableFrom(Class<?> module_type, Class<?> context_type, Class<?> return_type)
		{
			return this.type_tester.test(module_type, context_type, return_type);
		}
		
		@Override
		public V apply(WorldContext<M, E> context)
		{
			return this.getter.apply(context);
		}
		
		public static <M extends OreModule<?, M>, E, V> Variable<M, E, V> of(Class<M> module_type, Class<E> context_type, Class<V> return_type, Function<WorldContext<M, E>, V> getter)
		{
			return of(ContextualPredicate.of(module_type, context_type, return_type), getter);
		}
		
		public static <M extends OreModule<?, M>, E, V> Variable<M, E, V> of(ContextualPredicate<M, E, V> type_tester, Function<WorldContext<M, E>, V> getter)
		{
			return new Variable<M, E, V>(type_tester, getter);
		}
	}
}
