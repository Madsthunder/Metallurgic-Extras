package continuum.metalextras.loaders;

import continuum.api.metalextras.BlockOre;
import continuum.api.metalextras.OreCategory;
import continuum.api.metalextras.OreMaterial;
import continuum.essentials.hooks.ItemHooks;
import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ObjectLoader;
import continuum.metalextras.blocks.BlockCompressed;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BlockLoader implements ObjectLoader<MetalExtras_OH, MetalExtras_EH>
{
	
	@Override
	public void construction(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		
	}
	
	@Override
	public void pre(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		for(OreMaterial material : MetalExtras_OH.ores)
			for(OreCategory group : MetalExtras_OH.oreCategories)
			{
				BlockOre block = material.generateBlock(holder, group);
				ForgeRegistries.BLOCKS.register(block);
				ItemHooks.registerItemBlockMeta(block, block.getOreTypeProperty().getAllowedValues().size() - 1);
			}
		ForgeRegistries.BLOCKS.register(holder.copper_block = new BlockCompressed("copper"));
		ForgeRegistries.BLOCKS.register(holder.tin_block = new BlockCompressed("tin"));
		ForgeRegistries.BLOCKS.register(holder.aluminum_block = new BlockCompressed("aluminum"));
		ForgeRegistries.BLOCKS.register(holder.lead_block = new BlockCompressed("lead"));
		ForgeRegistries.BLOCKS.register(holder.silver_block = new BlockCompressed("silver"));
		ForgeRegistries.BLOCKS.register(holder.ender_block = new BlockCompressed("ender"));
		ForgeRegistries.BLOCKS.register(holder.sapphire_block = new BlockCompressed("sapphire"));
		ForgeRegistries.BLOCKS.register(holder.ruby_block = new BlockCompressed("ruby"));
	}
	
	@Override
	public String getName()
	{
		return "Blocks";
	}
}