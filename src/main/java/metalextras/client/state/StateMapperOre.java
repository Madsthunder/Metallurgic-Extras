package metalextras.client.state;

import api.metalextras.BlockOre;
import metalextras.MetalExtras;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;

public class StateMapperOre extends StateMapperBase
{
	public final MetalExtras objectHolder;
	
	public StateMapperOre(MetalExtras objectHolder)
	{
		this.objectHolder = objectHolder;
	}
	
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state)
	{
		BlockOre block = (BlockOre)state.getBlock();
		return new ModelResourceLocation("metalextras:" + block.getOreMaterial().getRegistryName().getResourcePath(), "type=" + block.getOreType(state).getName());
	}
}
