package metalextras.items;

import api.metalextras.BlockOre;
import api.metalextras.OreTypeProperty;
import metalextras.newores.NewOreType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;

public class ItemBlockOre extends ItemBlock
{
	private final OreTypeProperty property;
	public final NewOreType type;

	public ItemBlockOre(BlockOre block, OreTypeProperty property)
	{
		super(block);
		this.property = property;
		this.type = block.getOreType();
	}

	@Override
	public int getMetadata(int meta)
	{
		if(meta >= this.property.getAllowedValues().size())
			return 0;
		return meta;
	}

	@Override
	public CreativeTabs[] getCreativeTabs()
	{
		return this.type.getBlockModule().getCreativeTabs();
	}
}
