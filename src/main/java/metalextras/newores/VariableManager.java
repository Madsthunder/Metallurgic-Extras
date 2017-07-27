package metalextras.newores;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import metalextras.MetalExtras;
import metalextras.newores.modules.GenerationModule;
import metalextras.newores.modules.GenerationModule.Properties.GenerationPropertiesParser;
import metalextras.newores.modules.OreModule;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VariableManager
{
    private static final Supplier<Object> EMPTY = () -> null;
    private static final Map<ResourceLocation, Supplier<Boolean>> BOOLEAN_CONSTANTS = Maps.newHashMap();
    private static final Map<ResourceLocation, Supplier<Number>> NUMBER_CONSTANTS = Maps.newHashMap();
    private static final Map<ResourceLocation, Supplier<String>> STRING_CONSTANTS = Maps.newHashMap();
    private static final Map<ResourceLocation, Supplier<Object>> OBJECT_CONSTANTS = Maps.newHashMap();
    
    private static final Map<ResourceLocation, BiFunction<World, BlockPos, Object>> VARIABLES = Maps.newHashMap();
    
    private static final Map<ResourceLocation, Function<GenerationModule, BiFunction<World, BlockPos, GenerationModule.Properties>>> GENERATION_PROPERTIES = Maps.newHashMap();
    private static final Map<ResourceLocation, GenerationPropertiesParser> PROPERTIES_PARSERS = Maps.newHashMap();

    private static final Map<Class<?>, String> MASTER_MODULES = Maps.newHashMap();
    private static final Map<Class<?>, BiFunction<ResourceLocation, JsonObject, ? extends OreModule<?, ?>>> MASTER_MODULE_FACTORIES = Maps.newHashMap();
    private static final Map<Class<?>, String> MODULES = Maps.newHashMap();
    private static final Map<Class<?>, BiFunction<String, JsonObject, ? extends OreModule<?, ?>>> MODULE_FACTORIES = Maps.newHashMap();
    
    public static void register(ResourceLocation name, BiFunction<World, BlockPos, Object> getter)
    {
        VARIABLES.put(name, getter);
    }
    
    public static BiFunction<World, BlockPos, Object> getFunction(ResourceLocation name)
    {
        return VARIABLES.get(name);
    }
    
    public static void registerBooleanConstant(ResourceLocation name, boolean constant) { BOOLEAN_CONSTANTS.put(name, () -> constant); }
    public static void registerBooleanConstant(ResourceLocation name, Supplier<Boolean> constant) { BOOLEAN_CONSTANTS.put(name, constant); }
    public static Optional<Boolean> getBooleanConstant(ResourceLocation name) { return Optional.ofNullable((Boolean)Optional.ofNullable(Optional.<Supplier<?>>of(BOOLEAN_CONSTANTS.get(name)).orElseGet(() -> Optional.ofNullable(OBJECT_CONSTANTS.get(name)).orElse(EMPTY)).get()).filter((object) -> object instanceof Boolean).get()); }

    public static void registerNumberConstant(ResourceLocation name, Number constant) { NUMBER_CONSTANTS.put(name, () -> constant); }
    public static void registerNumberConstant(ResourceLocation name, Supplier<Number> constant) { NUMBER_CONSTANTS.put(name, constant); }
    public static Optional<Number> getNumberConstant(ResourceLocation name) { return Optional.ofNullable((Number)Optional.ofNullable(Optional.<Supplier<?>>ofNullable(NUMBER_CONSTANTS.get(name)).orElseGet(() -> Optional.ofNullable(OBJECT_CONSTANTS.get(name)).orElse(EMPTY)).get()).filter((object) -> object instanceof Number).get()); }

    public static void registerStringConstant(ResourceLocation name, String constant) { STRING_CONSTANTS.put(name, () -> constant); }
    public static void registerStringConstant(ResourceLocation name, Supplier<String> constant) { STRING_CONSTANTS.put(name, constant); }
    public static Optional<String> getStringConstant(ResourceLocation name) { return Optional.ofNullable((String)Optional.ofNullable(Optional.<Supplier<?>>of(STRING_CONSTANTS.get(name)).orElseGet(() -> Optional.ofNullable(OBJECT_CONSTANTS.get(name)).orElse(EMPTY)).get()).filter((object) -> object instanceof String).get()); }

    public static void registerConstant(ResourceLocation name, Object constant) { OBJECT_CONSTANTS.put(name, () -> constant); }
    public static void registerConstant(ResourceLocation name, Supplier<Object> constant) { OBJECT_CONSTANTS.put(name, constant); }
    public static <V> Optional<V> getConstant(ResourceLocation name, Class<V> clasz) { return Optional.ofNullable(Optional.ofNullable(Optional.ofNullable(OBJECT_CONSTANTS.get(name)).orElse(EMPTY).get()).filter((object) -> clasz.isInstance(object))); }
    
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

    public static <S extends OreModule<S, S>> void registerMasterModuleFactory(Class<S> module_type, String module_name, BiFunction<ResourceLocation, JsonObject, S> module_factory)
    {
    	MASTER_MODULES.put(module_type, module_name);
    	MASTER_MODULE_FACTORIES.put(module_type, module_factory);
    }
    
    public static <S extends OreModule<?, S> >void registerModuleFactory(Class<S> module_type, String module_name, BiFunction<String, JsonObject, S> module_factory)
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
		return (S)MODULE_FACTORIES.get(module_type).apply(String.format("%s/%s", path, MODULES.get(module_type)), Optional.ofNullable(json.get(MODULES.get(module_type))).orElseGet(VariableManager::newJsonObject).getAsJsonObject());
	}
    
    public static <S extends OreModule<?, S>> String getModuleName(Class<S> module_type)
    {
    	return MODULES.get(module_type);
    }
	
	public static Stream<Entry<Class<?>, String>> masterModuleStream()
	{
		return MASTER_MODULES.entrySet().stream();
	}
	
	public static JsonObject newJsonObject()
	{
		return new JsonObject();
	}
}
