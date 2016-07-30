package continuum.api.metalextras;

import java.util.List;

import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IOreType extends Comparable<IOreType>, IStringSerializable
{
	default public Block getBlock()
	{
		return this.getState().getBlock();
	}
	
	public IBlockState getState();
	public Float getHardness();
	public Float getResistance();
	public ResourceLocation getCategory();
	public ResourceLocation getResourceName();
	
	default public SoundType getSoundType()
	{
		return this.getBlock().getSoundType();
	}
	
	default public String getHarvestTool()
	{
		return this.getBlock().getHarvestTool(this.getState());
	}
	
	default public Integer getHarvestLevel()
	{
		return this.getBlock().getHarvestLevel(this.getState());
	}
	
	default public Boolean canFall()
	{
		return this.getBlock() instanceof BlockFalling;
	}
	
	default public int compareTo(IOreType o)
	{
		List<IOreType> registry = MetalExtras_OH.oreTypes;
		return registry.indexOf(this) - registry.indexOf(o);
	}
	
	default public void handleEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		
	}
	
	default public AxisAlignedBB getSelectionBox(World world, BlockPos pos)
	{
		return Block.FULL_BLOCK_AABB;
	}
	
	public static class Impl implements IOreType
	{
		private final ResourceLocation name;
		private final ResourceLocation category;
		private final IBlockState state;
		private final Float hardness;
		private final Float resistance;
		
		public Impl(String name, String category, IBlockState state, Float hardness, Float resistance)
		{
			this.name = new ResourceLocation(name);
			this.category = new ResourceLocation(category);
			this.state = state;
			this.hardness = hardness;
			this.resistance = resistance * 3;
		}

		@Override
		public String getName()
		{
			return this.getResourceName().getResourcePath();
		}
		
		@Override
		public ResourceLocation getResourceName()
		{
			return this.name;
		}
		
		@Override
		public ResourceLocation getCategory()
		{
			return this.category;
		}
		
		@Override
		public IBlockState getState()
		{
			return this.state;
		}

		@Override
		public Float getHardness()
		{
			return this.hardness;
		}

		@Override
		public Float getResistance()
		{
			return this.resistance * 5;
		}
	}
}
