package metalextras.newores;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import metalextras.MetalExtras;
import metalextras.newores.NewOreType.Generation;
import metalextras.newores.NewOreType.Generation.Properties.GenerationPropertiesParser;
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
    
    private static final Map<ResourceLocation, Function<Generation, BiFunction<World, BlockPos, Generation.Properties>>> GENERATION_PROPERTIES = Maps.newHashMap();
    private static final Map<ResourceLocation, GenerationPropertiesParser> PROPERTIES_PARSERS = Maps.newHashMap();
    
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
    public static Optional<Boolean> getBooleanConstant(ResourceLocation name) { return Optional.ofNullable((Boolean)Optional.ofNullable(Optional.<Supplier>of(BOOLEAN_CONSTANTS.get(name)).orElseGet(() -> Optional.ofNullable(OBJECT_CONSTANTS.get(name)).orElse(EMPTY)).get()).filter((object) -> object instanceof Boolean).get()); }

    public static void registerNumberConstant(ResourceLocation name, Number constant) { NUMBER_CONSTANTS.put(name, () -> constant); }
    public static void registerNumberConstant(ResourceLocation name, Supplier<Number> constant) { NUMBER_CONSTANTS.put(name, constant); }
    public static Optional<Number> getNumberConstant(ResourceLocation name) { return Optional.ofNullable((Number)Optional.ofNullable(Optional.<Supplier>ofNullable(NUMBER_CONSTANTS.get(name)).orElseGet(() -> Optional.ofNullable(OBJECT_CONSTANTS.get(name)).orElse(EMPTY)).get()).filter((object) -> object instanceof Number).get()); }

    public static void registerStringConstant(ResourceLocation name, String constant) { STRING_CONSTANTS.put(name, () -> constant); }
    public static void registerStringConstant(ResourceLocation name, Supplier<String> constant) { STRING_CONSTANTS.put(name, constant); }
    public static Optional<String> getStringConstant(ResourceLocation name) { return Optional.ofNullable((String)Optional.ofNullable(Optional.<Supplier>of(STRING_CONSTANTS.get(name)).orElseGet(() -> Optional.ofNullable(OBJECT_CONSTANTS.get(name)).orElse(EMPTY)).get()).filter((object) -> object instanceof String).get()); }

    public static void registerConstant(ResourceLocation name, Object constant) { OBJECT_CONSTANTS.put(name, () -> constant); }
    public static void registerConstant(ResourceLocation name, Supplier<Object> constant) { OBJECT_CONSTANTS.put(name, constant); }
    public static <V> Optional<V> getConstant(ResourceLocation name, Class<V> clasz) { return Optional.ofNullable(Optional.ofNullable(Optional.ofNullable(OBJECT_CONSTANTS.get(name)).orElse(EMPTY).get()).filter((object) -> clasz.isInstance(object))); }
    
    public static void registerConstantGenerationProperties(ResourceLocation name, Function<Generation, BiFunction<World, BlockPos, Generation.Properties>> getter)
    {
    	GENERATION_PROPERTIES.put(name, getter);
    }
    
    public static Optional<BiFunction<World, BlockPos, Generation.Properties>> getConstantGenerationProperties(ResourceLocation name, Generation generation)
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
}
