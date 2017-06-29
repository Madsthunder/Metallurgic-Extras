package continuum.essentials.client.state;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;

public class StateMapperStatic extends StateMapperBase implements ItemMeshDefinition
{
	private final ModelResourceLocation location;
	
	public StateMapperStatic(ModelResourceLocation location)
	{
		this.location = location;
	}
	
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state)
	{
		return this.location;
	}

	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack)
	{
		return this.location;
	}
	
	public static StateMapperStatic create(ModelResourceLocation location)
	{
		return new StateMapperStatic(location);
	}
}
