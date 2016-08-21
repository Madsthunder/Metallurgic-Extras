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

public class OreType implements Comparable<OreType>, IStringSerializable
{
	private final ResourceLocation name;
	private OreCategory category;
	private final IBlockState state;
	private final float hardness;
	private final float resistance;
	
	public OreType(String name, IBlockState state, float hardness, float resistance)
	{
		this.name = new ResourceLocation(name);
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
	
	public final String getName()
	{
		ResourceLocation name = this.getResourceName();
		return name.getResourceDomain() + "_" + name.getResourcePath();
	}
	
	public ResourceLocation getResourceName()
	{
		return this.name;
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
}
