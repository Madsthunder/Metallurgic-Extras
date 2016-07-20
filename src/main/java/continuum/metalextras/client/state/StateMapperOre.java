package continuum.metalextras.client.state;

import continuum.essentials.mod.CTMod;
import continuum.metalextras.blocks.BlockOreGround;
import continuum.metalextras.blocks.BlockOreGround.EnumGroundType;
import continuum.metalextras.blocks.BlockOreRock.EnumRockType;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class StateMapperOre extends StateMapperBase implements ItemMeshDefinition
{
	public final MetalExtras_OH objectHolder;
	
	public StateMapperOre(MetalExtras_OH objectHolder)
	{
		this.objectHolder = objectHolder;
	}
	
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state)
	{
		Block block = state.getBlock();
		Boolean groundBlock = block instanceof BlockOreGround;
		String uname = block.getUnlocalizedName().substring(5);
		IStringSerializable[] strings = groundBlock ? EnumGroundType.values() : EnumRockType.values();
		String tname = "type=" + strings[state.getBlock().getMetaFromState(state)];
		Integer subString = uname.length() - (groundBlock ? 7 : 5);
		return new ModelResourceLocation("metalextras:" + uname.substring(0, subString), tname);
	}

	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack)
	{
		return new ModelResourceLocation("metalextras:" + this.objectHolder.oresList.keySet().toArray(new String[]{})[stack.getMetadata()]);
	}
	
	
}
