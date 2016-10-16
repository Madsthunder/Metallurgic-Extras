package continuum.api.metalextras;

import java.util.List;

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
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

public class OreType implements Comparable<OreType>, IStringSerializable, IForgeRegistryEntry<OreType>
{
	private ResourceLocation name;
	private OreCategory category;
	private final IBlockState state;
	private final float hardness;
	private final float resistance;
	
	public OreType(IBlockState state, float hardness, float resistance)
	{
		this.state = state;
		this.hardness = hardness;
		this.resistance = resistance * 3;
	}
	
	public Block getBlock()
	{
		return this.getState().getBlock();
	}
	
	public IBlockState getState()
	{
		return this.state;
	}
	
	public float getHardness()
	{
		return this.hardness;
	}
	
	public float getResistance()
	{
		return this.resistance * 5;
	}
	
	public boolean setCategory(OreCategory category)
	{
		if(this.category == null)
			this.category = category;
		else
			return false;
		return true;
	}
	
	public OreCategory getCategory()
	{
		return this.category;
	}
	
	@Override
	public final String getName()
	{
		ResourceLocation name = this.getRegistryName();
		return name.getResourceDomain() + "_" + name.getResourcePath();
	}
	
	public SoundType getSoundType()
	{
		return this.getBlock().getSoundType();
	}
	
	public String getHarvestTool()
	{
		return this.getBlock().getHarvestTool(this.getState());
	}
	
	public int getHarvestLevel()
	{
		return this.getBlock().getHarvestLevel(this.getState());
	}
	
	public boolean canFall()
	{
		return this.getBlock() instanceof BlockFalling;
	}
	
	public int compareTo(OreType o)
	{
		List<OreType> list = OreCategory.getAllOreTypes();
		return list.indexOf(this) - list.indexOf(o);
	}
	
	public void handleEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		
	}
	
	public AxisAlignedBB getSelectionBox(World world, BlockPos pos)
	{
		return Block.FULL_BLOCK_AABB;
	}

	public final OreType setRegistryName(String modid, String name)
	{
		return this.setRegistryName(new ResourceLocation(modid, name));
	}
	
	public final OreType setRegistryName(String name)
	{
        return this.setRegistryName(new ResourceLocation(name));
    }

	@Override
	public final OreType setRegistryName(ResourceLocation name)
	{
        if(this.getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + this.getRegistryName());
        this.name = name;
		return this;
	}
	
	@Override
	public final ResourceLocation getRegistryName()
	{
		return this.name;
	}

	@Override
	public final Class<? super OreType> getRegistryType()
	{
		return OreType.class;
	}	
}
