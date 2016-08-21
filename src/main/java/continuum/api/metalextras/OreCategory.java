package continuum.api.metalextras;

import java.util.List;

import com.google.common.collect.Lists;

import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.util.ResourceLocation;

public class OreCategory
{
	private final ResourceLocation name;
	private final List<OreType> oreTypes = Lists.newArrayList();
	
	public OreCategory(String name)
	{
		this.name = new ResourceLocation(name);
	}
	
	public ResourceLocation getName()
	{
		return this.name;
	}
	
	public boolean addOreType(OreType type)
	{
		if(this.oreTypes.size() <= 16 && type.setCategory(this))
			this.oreTypes.add(type);
		else
			return false;
		return true;
	}
	
	public List<OreType> getOreTypes()
	{
		return Lists.newArrayList(this.oreTypes);
	}
	
	public static List<OreType> getAllOreTypes()
	{
		List<OreType> types = Lists.newArrayList();
		for(OreCategory category : MetalExtras_OH.oreCategories)
			types.addAll(category.getOreTypes());
		return types;
	}
}
