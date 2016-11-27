package metalextras.items;

import api.metalextras.OreTypeProperty;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockOre extends ItemBlock
{
	private final OreTypeProperty property;
	
	public ItemBlockOre(Block block, OreTypeProperty property)
	{
		super(block);
		this.property = property;
	}
	
	@Override
	public int getMetadata(int meta)
	{
		if(meta >= this.property.getAllowedValues().size())
			return 0;
		return meta;
	}
}
