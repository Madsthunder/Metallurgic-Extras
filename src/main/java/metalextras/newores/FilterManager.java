package metalextras.newores;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import api.metalextras.OreType;
import net.minecraft.util.ResourceLocation;

public class FilterManager
{
	public static final Map<ResourceLocation, BiConsumer<Set<OreType>, Set<String>>> FILTERS = Maps.newHashMap();

	public static void register(ResourceLocation name, BiConsumer<Set<OreType>, Set<String>> filter)
	{
		FILTERS.put(name, filter);
	}

	@Nullable
	public static BiConsumer<Set<OreType>, Set<String>> getFilter(ResourceLocation name)
	{
		return FILTERS.get(name);
	}
}
