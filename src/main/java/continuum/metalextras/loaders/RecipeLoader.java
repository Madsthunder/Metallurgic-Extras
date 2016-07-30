package continuum.metalextras.loaders;

import java.util.List;

import com.google.common.collect.Lists;

import continuum.api.metalextras.IOreData;
import continuum.essentials.helpers.ObjectHelper;
import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ObjectLoader;
import continuum.metalextras.blocks.BlockOre;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeLoader implements ObjectLoader<MetalExtras_OH, MetalExtras_EH>
{
	@Override
	public void init(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		for(IOreData data : holder.ores)
		{
			List<ItemStack> stacks = Lists.newArrayList();
			ItemStack ingot = data.getIngot();
			Integer i = 0;
			String name = data.getOreName().getResourcePath().split("_")[0];
			name = "ore" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
			for(BlockOre ore : data.getOre().getOreBlocks())
				for(Integer j : ObjectHelper.increment(ore.getOreTypeProperty().getAllowedValues().size()))
				{
					ItemStack stack = new ItemStack(ore, 1, j);
					stacks.add(stack);
					OreDictionary.registerOre(name, stack);
				}
			ItemStack stack;
			OreDictionary.registerOre(name, stack = new ItemStack(holder.ore, 1, holder.ores.indexOf(data)));
			FurnaceRecipes.instance().addSmeltingRecipe(stack, ingot, 0);
			for(ItemStack stack1 : stacks)
				FurnaceRecipes.instance().addSmeltingRecipe(stack1, ingot, 0);
		}
		OreDictionary.registerOre("blockCopper", holder.copper_block);
		OreDictionary.registerOre("blockMetal", holder.copper_block);
		OreDictionary.registerOre("blockTin", holder.tin_block);
		OreDictionary.registerOre("blockMetal", holder.tin_block);
		OreDictionary.registerOre("blockAluminum", holder.aluminum_block);
		OreDictionary.registerOre("blockMetal", holder.aluminum_block);
		OreDictionary.registerOre("blockLead", holder.lead_block);
		OreDictionary.registerOre("blockMetal", holder.lead_block);
		OreDictionary.registerOre("blockSilver", holder.silver_block);
		OreDictionary.registerOre("blockMetal", holder.silver_block);
		OreDictionary.registerOre("blockEnder", holder.ender_block);
		OreDictionary.registerOre("blockSapphire", holder.sapphire_block);
		OreDictionary.registerOre("blockRuby", holder.ruby_block);
		
		OreDictionary.registerOre("ingotCopper", holder.copper_ingot);
		OreDictionary.registerOre("ingotTin", holder.tin_ingot);
		OreDictionary.registerOre("ingotAluminum", holder.aluminum_ingot);
		OreDictionary.registerOre("ingotLead", holder.lead_ingot);
		OreDictionary.registerOre("ingotSilver", holder.silver_ingot);
		OreDictionary.registerOre("gemEnder", holder.ender_gem);
		OreDictionary.registerOre("gemSapphire", holder.sapphire_gem);
		OreDictionary.registerOre("gemRuby", holder.ruby_gem);
		
		registerCompressedRecipe(holder.copper_block, holder.copper_ingot);
		registerCompressedRecipe(holder.tin_block, holder.tin_ingot);
		registerCompressedRecipe(holder.aluminum_block, holder.aluminum_ingot);
		registerCompressedRecipe(holder.lead_block, holder.lead_ingot);
		registerCompressedRecipe(holder.silver_block, holder.silver_ingot);
		registerCompressedRecipe(holder.ender_block, holder.ender_gem);
		registerCompressedRecipe(holder.sapphire_block, holder.sapphire_gem);
		registerCompressedRecipe(holder.ruby_block, holder.ruby_gem);
	}
	
	private void registerCompressedRecipe(Block block, Item item)
	{
		GameRegistry.addShapedRecipe(new ItemStack(block, 1), "III", "III", "III", 'I', item);
		GameRegistry.addShapelessRecipe(new ItemStack(item, 9), block);
	}
	
	@Override
	public String getName()
	{
		return "Recipes";
	}

}