package continuum.metalextras.items;

import java.util.List;

import continuum.essentials.mod.CTMod;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemOre extends Item
{
	public final MetalExtras_OH objectHolder;
	
	public ItemOre(MetalExtras_OH objectHolder)
	{
		this.objectHolder = objectHolder;
		this.setHasSubtypes(true);
		this.setRegistryName("ore");
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return "tile." + this.objectHolder.ores.get(stack.getMetadata());
	}
	
	@Override
	public int getMetadata(int meta)
	{
		return meta;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		for(Integer i = 0; i < this.objectHolder.ores.size(); i++)
			list.add(new ItemStack(item, 1, i));
	}
}
