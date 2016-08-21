package continuum.metalextras.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class BlockCompressed extends Block
{
	public final String name;
	
	public BlockCompressed(String name)
	{
		super(Material.IRON);
		this.name = name;
		this.setUnlocalizedName(name + "_block");
		this.setRegistryName(name + "_block");
	}
	
	public int getMetaFromState(IBlockState state)
	{
		return 0;
	}
}
