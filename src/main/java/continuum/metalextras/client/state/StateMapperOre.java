package continuum.metalextras.client.state;

import continuum.api.metalextras.BlockOre;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;

public class StateMapperOre extends StateMapperBase
{
	public final MetalExtras_OH objectHolder;
	
	public StateMapperOre(MetalExtras_OH objectHolder)
	{
		this.objectHolder = objectHolder;
	}
	
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state)
	{
		BlockOre block = (BlockOre)state.getBlock();
		return new ModelResourceLocation("metalextras:" + block.getOreData().getName().getResourcePath(), "type=" + block.getOreType(state).getName());
	}
}
