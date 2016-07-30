package continuum.api.metalextras;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import continuum.metalextras.blocks.BlockOre;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;

public interface IOre
{
	public ResourceLocation getName();
	
	@Nullable
	default public IBlockState applyBlockState(IOreType type)
	{
		for(BlockOre ore : this.getOreBlocks())
			if(ore.containsOreType(type))
				return ore.withOreType(type);
		return null;
	}
	public void setOreProperties(OreProperties properties);
	public OreProperties getOreProperties();
	public OrePredicate getPredicate();
	public List<BlockOre> getOreBlocks();
	
	public static class Impl implements IOre
	{
		private final ResourceLocation name;
		private final MetalExtras_OH holder;
		private final List<BlockOre> blocks;
		private OreProperties properties;
		private OrePredicate predicate;
		
		public Impl(MetalExtras_OH holder, ResourceLocation name, BlockOre... blocks)
		{
			this.name = name;
			this.holder = holder;
			this.blocks = Lists.newArrayList(blocks);
		}
		
		@Override
		public ResourceLocation getName()
		{
			return this.name;
		}
		
		@Override
		public void setOreProperties(OreProperties properties)
		{
			this.properties = properties;
		}

		@Override
		public OreProperties getOreProperties()
		{
			return this.properties;
		}
		
		@Override
		public OrePredicate getPredicate()
		{
			return this.predicate == null ? holder.orePredicates.get(this.name) : this.predicate;
		}

		@Override
		public List<BlockOre> getOreBlocks()
		{
			return Lists.newArrayList(this.blocks);
		}
		
	}
}
