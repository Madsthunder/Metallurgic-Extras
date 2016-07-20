package continuum.metalextras.items;

import continuum.metalextras.blocks.BlockOreGround;
import continuum.metalextras.blocks.BlockOreGround.EnumGroundType;
import continuum.metalextras.blocks.BlockOreRock.EnumRockType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class ItemBlockOre extends ItemBlock
{
	public ItemBlockOre(Block block)
	{
		super(block);
		this.setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int meta)
	{
		return meta;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		String uname = this.block.getUnlocalizedName();
		return uname.substring(0, uname.length() - (this.block instanceof BlockOreGround ? 7 : 5));
	}
}
