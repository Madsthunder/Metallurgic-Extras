package metalextras.newores;

import java.util.Map;
import java.util.function.BiFunction;

import com.google.common.collect.Maps;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VariableManager
{
    private static final Map<ResourceLocation, BiFunction<World, BlockPos, Object>> VARIABLE_GETTERS = Maps.newHashMap();
    
    public static void register(ResourceLocation name, BiFunction<World, BlockPos, Object> getter)
    {
        VARIABLE_GETTERS.put(name, getter);
    }
    
    public static BiFunction<World, BlockPos, Object> getFunction(ResourceLocation name)
    {
        return VARIABLE_GETTERS.get(name);
    }
}
