package continuum.metalextras.items;

import continuum.essentials.mod.CTMod;
import continuum.metalextras.blocks.BlockCompressed;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockCompressed extends ItemBlock
{	
	public ItemBlockCompressed(BlockCompressed block)
	{
		super(block);
		this.setUnlocalizedName(this.block.getUnlocalizedName());
		this.setRegistryName(this.block.getRegistryName());
	}
}
