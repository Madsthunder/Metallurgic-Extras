package metalextras.items;

import java.util.Arrays;

import api.metalextras.OreUtils;
import metalextras.newores.NewOreType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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
		NewOreType material = OreUtils.getTypesRegistry().getById(stack.getMetadata());
		return material == null ? NewOreType.DEFAULT_NAME : material.getName();
	}
	
	@Override
	public int getMetadata(int meta)
	{
		return meta;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
	{
	    if(tab == CreativeTabs.SEARCH)
	        OreUtils.getTypesRegistry().forEach((type) -> list.add(OreUtils.getItemStackForMaterial(type)));
	    
		for(NewOreType material : OreUtils.getTypesRegistry())
		    for(CreativeTabs type_tab : material.getItemCreativeTabs())
		        if(tab == type_tab)
		        {
		            list.add(OreUtils.getItemStackForMaterial(material));
		            break;
		        }
	}
	
	@Override
	public CreativeTabs[] getCreativeTabs()
	{
	    int creative_tabs_size = 0;
	    CreativeTabs[] creative_tab_array = new CreativeTabs[CreativeTabs.CREATIVE_TAB_ARRAY.length];
		for(NewOreType material : OreUtils.getTypesRegistry())
		    for(CreativeTabs tab : material.getItemCreativeTabs())
		        for(int i = 0; i <= creative_tabs_size; i++)
		        {
		            CreativeTabs tab1 = creative_tab_array[i];
		            if(tab == tab1)
		                break;
		            if(tab == null)
		            {
		                creative_tab_array[i] = tab;
		                creative_tabs_size++;
		            }
		        }
		return Arrays.copyOfRange(creative_tab_array , 0, creative_tabs_size);
	}
	
}
