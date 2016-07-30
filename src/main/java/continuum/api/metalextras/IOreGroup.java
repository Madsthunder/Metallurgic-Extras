package continuum.api.metalextras;

import java.util.List;

import com.google.common.collect.Lists;

import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.util.ResourceLocation;

public interface IOreGroup
{
	public ResourceLocation getName();
	public List<IOreType> getOreTypes();
	
	public static class Impl implements IOreGroup
	{
		private final ResourceLocation name;
		private final List<IOreType> oreTypes;
		
		public Impl(String name)
		{
			this.name = new ResourceLocation(name);
			List<IOreType> oreTypes = Lists.newArrayList();
			for(IOreType type : MetalExtras_OH.oreTypes)
				if(type.getCategory().equals(this.getName()))
					oreTypes.add(type);
			this.oreTypes = Lists.newArrayList(oreTypes);
		}

		@Override
		public ResourceLocation getName()
		{
			return this.name;
		}

		@Override
		public List<IOreType> getOreTypes()
		{
			return Lists.newArrayList(this.oreTypes);
		}
	}
}
