package api.metalextras;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import metalextras.newores.NewOreType;
import net.minecraft.util.ResourceLocation;

public final class OreTypeRegistry implements Iterable<NewOreType>
{
	private final BiMap<ResourceLocation, NewOreType> name_to_ore_type = HashBiMap.create();
	private final List<NewOreType> ids = Lists.newArrayList();

	public void register(NewOreType... types)
	{
		for(NewOreType type : types)
		{
			this.name_to_ore_type.put(type.registry_name, type);
			this.ids.add(type);
		}
	}

	public int getIdFor(NewOreType type)
	{
		return this.ids.indexOf(type);
	}

	@Nullable
	public NewOreType getById(int id)
	{
		return id >= this.ids.size() ? null : this.ids.get(id);
	}

	public int nextId()
	{
		return ids.size();
	}

	public NewOreType getTypeByName(ResourceLocation name)
	{
		return this.name_to_ore_type.get(name);
	}

	public List<NewOreType> getValues()
	{
		return Lists.newArrayList(this.ids);
	}

	public void clear()
	{
		this.ids.clear();
	}

	@Override
	public Iterator<NewOreType> iterator()
	{
		return this.ids.iterator();
	}
}
