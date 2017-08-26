package api.metalextras;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import metalextras.newores.NewOreMaterial;
import net.minecraft.util.ResourceLocation;

public final class OreMaterialsRegistry implements Iterable<NewOreMaterial>
{
	private final BiMap<ResourceLocation, NewOreMaterial> name_to_ore_material = HashBiMap.create();
	private final List<NewOreMaterial> ids = Lists.newArrayList();

	public void register(NewOreMaterial... materials)
	{
		for(NewOreMaterial material : materials)
		{
			this.name_to_ore_material.put(material.getRegistryName(), material);
			this.ids.add(material);
		}
	}

	public int getIdFor(NewOreMaterial materials)
	{
		return this.ids.indexOf(materials);
	}

	@Nullable
	public NewOreMaterial getById(int id)
	{
		return id >= this.ids.size() ? null : this.ids.get(id);
	}

	public int nextId()
	{
		return ids.size();
	}

	public NewOreMaterial getTypeByName(ResourceLocation name)
	{
		return this.name_to_ore_material.get(name);
	}

	public List<NewOreMaterial> getValues()
	{
		return Lists.newArrayList(this.ids);
	}

	public void clear()
	{
		this.ids.clear();
	}

	@Override
	public Iterator<NewOreMaterial> iterator()
	{
		return this.ids.iterator();
	}
}
