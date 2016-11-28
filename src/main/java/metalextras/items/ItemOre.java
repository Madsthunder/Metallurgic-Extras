package metalextras.items;

import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import api.metalextras.BlockOre;
import api.metalextras.OreMaterial;
import api.metalextras.OreUtils;
import metalextras.MetalExtras;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemOre extends Item
{
	public ItemOre()
	{
		this.setHasSubtypes(true);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		OreMaterial material = OreUtils.getMaterialsRegistry().getValues().get(stack.getMetadata());
		return material == null ? "tile.metalextras:unknown" : material.getLanguageKey();
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
			for (OreMaterial material : OreUtils.getMaterialsRegistry())
				if(tab == tab.SEARCH || material.getCreativeTab() == tab)
					list.add(new ItemStack(item, 1, OreUtils.getMaterialsRegistry().getValues().indexOf(material)));
	}
	
	@Override
	public CreativeTabs[] getCreativeTabs()
	{
		List<CreativeTabs> tabs = Lists.newArrayList();
			for (OreMaterial material : OreUtils.getMaterialsRegistry())
			{
				CreativeTabs tab = material.getCreativeTab();
				if(tab != null && !tabs.contains(tab))
					tabs.add(tab);
			}
		return Iterables.toArray(tabs, CreativeTabs.class);
	}
	
}
