package continuum.metalextras.client.state;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;

public class StateMapperCompressed extends StateMapperBase
{
	@Override
	public ModelResourceLocation getModelResourceLocation(IBlockState state)
	{
		return new ModelResourceLocation("metalextras:compressed", state.getBlock().getUnlocalizedName().substring(5));
	}
}
