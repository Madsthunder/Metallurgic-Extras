package continuum.metalextras.loaders;

import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ObjectLoader;
import continuum.metalextras.blocks.BlockCompressed;
import continuum.metalextras.items.ItemBlockCompressed;
import continuum.metalextras.items.ItemOre;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ItemLoader implements ObjectLoader<MetalExtras_OH, MetalExtras_EH>
{
	@Override
	public void pre(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		ForgeRegistries.ITEMS.register(holder.ore = new ItemOre(holder));
		holder.copper_ingot = this.registerIngot("copper");
		holder.tin_ingot = this.registerIngot("tin");
		holder.aluminum_ingot = this.registerIngot("aluminum");
		holder.lead_ingot = this.registerIngot("lead");
		holder.silver_ingot = this.registerIngot("silver");
		holder.ender_gem = this.registerGem("ender");
		holder.sapphire_gem = this.registerGem("sapphire");
		holder.ruby_gem = this.registerGem("ruby");
		registerCompressed(holder.copper_block);
		registerCompressed(holder.tin_block);
		registerCompressed(holder.aluminum_block);
		registerCompressed(holder.lead_block);
		registerCompressed(holder.silver_block);
		registerCompressed(holder.ender_block);
		registerCompressed(holder.sapphire_block);
		registerCompressed(holder.ruby_block);
	}
	
	private Item registerIngot(String name)
	{
		Item item = new Item().setUnlocalizedName(name + "_ingot").setRegistryName(name + "_ingot");
		ForgeRegistries.ITEMS.register(item);
		return item;
	}
	
	private Item registerGem(String name)
	{
		Item item = new Item().setUnlocalizedName(name + "_gem").setRegistryName(name + "_gem");
		ForgeRegistries.ITEMS.register(item);
		return item;
	}
	
	private ItemBlockCompressed registerCompressed(BlockCompressed block)
	{
		ItemBlockCompressed item = new ItemBlockCompressed(block);
		ForgeRegistries.ITEMS.register(item);
		return item;
	}
	
	@Override
	public String getName()
	{
		return "Items";
	}
}
