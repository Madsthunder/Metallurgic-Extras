package metalextras.blocks;

import metalextras.tileentity.TileEntityTeleporter;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTeleporter extends BlockContainer
{
	public static final PropertyBool on = PropertyBool.create("on");
	public static final PropertyEnum<EnumDyeColor> color = PropertyEnum.create("color", EnumDyeColor.class);
	
	public BlockTeleporter()
	{
		super(Material.ROCK);
		this.setDefaultState(this.getDefaultState().withProperty(on, false).withProperty(color, EnumDyeColor.WHITE));
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess access, BlockPos pos)
	{
		TileEntity entity = access.getTileEntity(pos);
		if(entity instanceof TileEntityTeleporter)
			return state.withProperty(on, ((TileEntityTeleporter)entity).other != null);
		return state;
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(color).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(color, EnumDyeColor.values()[meta]);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityTeleporter();
	}
	
}
