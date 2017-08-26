package api.metalextras;

import java.util.List;
import java.util.Locale;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;

public class ModelType implements Comparable<ModelType>
{
	private static final BiMap<String, ModelType> types = HashBiMap.create();
	public static final ModelType DEFAULT = create("default", new ResourceLocation("missingno"));
	public static final ModelType IRON = create("iron", new ResourceLocation("metalextras:block/iron_ore"));
	public static final ModelType EMERALD = create("emerald", new ResourceLocation("metalextras:block/emerald_ore"));
	public static final ModelType LAPIS = create("lapis", new ResourceLocation("metalextras:block/lapis_ore"));
	private static int current_index;
	public final int index;
	public final ResourceLocation model_location;

	private ModelType(String name, ResourceLocation model_location)
	{
		types.put(name, this);
		this.index = current_index++;
		this.model_location = model_location;
	}

	public final String getName()
	{
		return types.inverse().get(this);
	}

	public static ModelType create(String name, ResourceLocation model_location)
	{
		return new ModelType(name, model_location);
	}

	public static ModelType byName(String name)
	{
		name = name.toLowerCase(Locale.ENGLISH);
		ModelType type = allTypes().get(name);
		if(type != null)
			return type;
		return null;
	}

	public static BiMap<String, ModelType> allTypes()
	{
		return HashBiMap.create(types);
	}

	public static List<ModelType> values()
	{
		return Lists.newArrayList(types.values());
	}

	@Override
	public int compareTo(ModelType type)
	{
		return compare(this, type);
	}
	
	public static int compare(ModelType type, ModelType other)
	{
		return Integer.compare(type.index, other.index);
	}
}
