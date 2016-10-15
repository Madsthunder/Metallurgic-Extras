package continuum.api.metalextras;

import java.util.List;

import com.google.common.collect.Lists;

import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

public class OreCategory implements IForgeRegistryEntry<OreCategory>
{
    private ResourceLocation registryName = null;
	private final List<OreType> oreTypes = Lists.newArrayList();
	
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

	public final OreCategory setRegistryName(String modid, String name)
	{
		return this.setRegistryName(new ResourceLocation(modid, name));
	}
	
	public final OreCategory setRegistryName(String name)
	{
        return this.setRegistryName(new ResourceLocation(name));
    }

	@Override
	public final OreCategory setRegistryName(ResourceLocation name)
	{
        if(this.getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + this.getRegistryName());
        this.registryName = name;
		return this;
	}
	
	@Override
	public final ResourceLocation getRegistryName()
	{
		return this.registryName;
	}

	@Override
	public final Class<? super OreCategory> getRegistryType()
	{
		return OreCategory.class;
	}
}
