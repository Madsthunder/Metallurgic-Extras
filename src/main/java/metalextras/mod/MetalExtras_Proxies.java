package metalextras.mod;

import com.google.common.base.Functions;

import api.metalextras.BlockOre;
import api.metalextras.OreMaterial;
import api.metalextras.OreUtils;
import continuum.core.mod.CTCore_OH;
import continuum.essentials.client.state.StateMapperStatic;
import metalextras.MetalExtras;
import metalextras.client.model.ModelOre;
import metalextras.world.gen.OreGeneration;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class MetalExtras_Proxies
{
	public static final Common I = FMLLaunchHandler.side() == Side.CLIENT ? new Client() : new Common();
	
	public static class Common
	{
		public void pre()
		{
			OreUtils.registerOreMaterialOrDctnry(MetalExtras.COPPER_ORE, "oreCopper", true);
			OreUtils.registerOreMaterialOrDctnry(MetalExtras.TIN_ORE, "oreTin", true);
			OreUtils.registerOreMaterialOrDctnry(MetalExtras.ALUMINUM_ORE, "oreAluminum", true);
			OreUtils.registerOreMaterialOrDctnry(MetalExtras.LEAD_ORE, "oreLead", true);
			OreUtils.registerOreMaterialOrDctnry(MetalExtras.SILVER_ORE, "oreSilver", true);
			OreUtils.registerOreMaterialOrDctnry(MetalExtras.ENDER_ORE, "oreEnder", true);
			OreUtils.registerOreMaterialOrDctnry(MetalExtras.SAPPHIRE_ORE, "oreSapphire", true);
			OreUtils.registerOreMaterialOrDctnry(MetalExtras.RUBY_ORE, "oreRuby", true);
			OreDictionary.registerOre("blockCopper", MetalExtras.COPPER_BLOCK);
			OreDictionary.registerOre("blockTin", MetalExtras.TIN_BLOCK);
			OreDictionary.registerOre("blockAluminum", MetalExtras.ALUMINUM_BLOCK);
			OreDictionary.registerOre("blockLead", MetalExtras.LEAD_BLOCK);
			OreDictionary.registerOre("blockSilver", MetalExtras.SILVER_BLOCK);
			OreDictionary.registerOre("blockEnder", MetalExtras.ENDER_BLOCK);
			OreDictionary.registerOre("blockSapphire", MetalExtras.SAPPHIRE_BLOCK);
			OreDictionary.registerOre("blockRuby", MetalExtras.RUBY_BLOCK);
			OreDictionary.registerOre("ingotCopper", MetalExtras.COPPER_INGOT);
			OreDictionary.registerOre("ingotTin", MetalExtras.TIN_INGOT);
			OreDictionary.registerOre("ingotAluminum", MetalExtras.ALUMINUM_INGOT);
			OreDictionary.registerOre("ingotLead", MetalExtras.LEAD_INGOT);
			OreDictionary.registerOre("ingotSilver", MetalExtras.SILVER_INGOT);
			OreDictionary.registerOre("gemEnder", MetalExtras.ENDER_GEM);
			OreDictionary.registerOre("gemSapphire", MetalExtras.SAPPHIRE_GEM);
			OreDictionary.registerOre("gemRuby", MetalExtras.RUBY_GEM);			
		}
		
		public void init()
		{
			OreUtils.registerOreMaterialSmelting(MetalExtras.COPPER_ORE, new ItemStack(MetalExtras.COPPER_INGOT), 0.7F, true);
			OreUtils.registerOreMaterialSmelting(MetalExtras.TIN_ORE, new ItemStack(MetalExtras.TIN_INGOT), 0.7F, true);
			OreUtils.registerOreMaterialSmelting(MetalExtras.ALUMINUM_ORE, new ItemStack(MetalExtras.ALUMINUM_INGOT), .5F, true);
			OreUtils.registerOreMaterialSmelting(MetalExtras.LEAD_ORE, new ItemStack(MetalExtras.LEAD_INGOT), .9F, true);
			OreUtils.registerOreMaterialSmelting(MetalExtras.SILVER_ORE, new ItemStack(MetalExtras.SILVER_INGOT), 0.9F, true);
			OreUtils.registerOreMaterialSmelting(MetalExtras.ENDER_ORE, new ItemStack(MetalExtras.ENDER_GEM), 1F, true);
			OreUtils.registerOreMaterialSmelting(MetalExtras.SAPPHIRE_ORE, new ItemStack(MetalExtras.SAPPHIRE_GEM), 1.5F, true);
			OreUtils.registerOreMaterialSmelting(MetalExtras.RUBY_ORE, new ItemStack(MetalExtras.RUBY_GEM), 1.5F, true);
			GameRegistry.addShapedRecipe(new ItemStack(MetalExtras.COPPER_BLOCK), "III", "III", "III", 'I', MetalExtras.COPPER_INGOT);
			GameRegistry.addShapelessRecipe(new ItemStack(MetalExtras.COPPER_INGOT, 9), MetalExtras.COPPER_BLOCK);
			GameRegistry.addShapedRecipe(new ItemStack(MetalExtras.TIN_BLOCK), "III", "III", "III", 'I', MetalExtras.ALUMINUM_INGOT);
			GameRegistry.addShapelessRecipe(new ItemStack(MetalExtras.TIN_INGOT, 9), MetalExtras.ALUMINUM_BLOCK);
			GameRegistry.addShapedRecipe(new ItemStack(MetalExtras.ALUMINUM_BLOCK), "III", "III", "III", 'I', MetalExtras.ALUMINUM_INGOT);
			GameRegistry.addShapelessRecipe(new ItemStack(MetalExtras.ALUMINUM_INGOT, 9), MetalExtras.ALUMINUM_BLOCK);
			GameRegistry.addShapedRecipe(new ItemStack(MetalExtras.LEAD_BLOCK), "III", "III", "III", 'I', MetalExtras.LEAD_INGOT);
			GameRegistry.addShapelessRecipe(new ItemStack(MetalExtras.LEAD_INGOT, 9), MetalExtras.LEAD_BLOCK);
			GameRegistry.addShapedRecipe(new ItemStack(MetalExtras.SILVER_BLOCK), "III", "III", "III", 'I', MetalExtras.SILVER_INGOT);
			GameRegistry.addShapelessRecipe(new ItemStack(MetalExtras.SILVER_INGOT, 9), MetalExtras.SILVER_BLOCK);
			GameRegistry.addShapedRecipe(new ItemStack(MetalExtras.ENDER_BLOCK), "III", "III", "III", 'I', MetalExtras.ENDER_GEM);
			GameRegistry.addShapelessRecipe(new ItemStack(MetalExtras.ENDER_GEM, 9), MetalExtras.ENDER_BLOCK);
			GameRegistry.addShapedRecipe(new ItemStack(MetalExtras.SAPPHIRE_BLOCK), "III", "III", "III", 'I', MetalExtras.SAPPHIRE_GEM);
			GameRegistry.addShapelessRecipe(new ItemStack(MetalExtras.SAPPHIRE_GEM, 9), MetalExtras.SAPPHIRE_BLOCK);
			GameRegistry.addShapedRecipe(new ItemStack(MetalExtras.RUBY_BLOCK), "III", "III", "III", 'I', MetalExtras.RUBY_GEM);
			GameRegistry.addShapelessRecipe(new ItemStack(MetalExtras.RUBY_GEM, 9), MetalExtras.RUBY_BLOCK);
			GameRegistry.registerWorldGenerator(new OreGeneration(), 100);
		}
	}
	
	private static class Client extends Common
	{
		@SideOnly(Side.CLIENT)
		public void pre()
		{
			super.pre();
			CTCore_OH.models.put(new ResourceLocation("metalextras:models/block/ore"), Functions.constant(new ModelOre()));
			MetalExtras.COPPER_ORE.setModel(new ResourceLocation("metalextras:block/copper_ore"));
			MetalExtras.TIN_ORE.setModel(new ResourceLocation("metalextras:block/tin_ore"));
			MetalExtras.ALUMINUM_ORE.setModel(new ResourceLocation("metalextras:block/aluminum_ore"));
			MetalExtras.LEAD_ORE.setModel(new ResourceLocation("metalextras:block/lead_ore"));
			MetalExtras.SILVER_ORE.setModel(new ResourceLocation("metalextras:block/silver_ore"));
			MetalExtras.ENDER_ORE.setModel(new ResourceLocation("metalextras:block/ender_ore"));
			MetalExtras.SAPPHIRE_ORE.setModel(new ResourceLocation("metalextras:block/sapphire_ore"));
			MetalExtras.RUBY_ORE.setModel(new ResourceLocation("metalextras:block/ruby_ore"));
			ModelLoader.setCustomModelResourceLocation(MetalExtras.COPPER_INGOT, 0, new ModelResourceLocation("metalextras:copper_ingot", "inventory"));
			ModelLoader.setCustomModelResourceLocation(MetalExtras.TIN_INGOT, 0, new ModelResourceLocation("metalextras:tin_ingot", "inventory"));
			ModelLoader.setCustomModelResourceLocation(MetalExtras.ALUMINUM_INGOT, 0, new ModelResourceLocation("metalextras:aluminum_ingot", "inventory"));
			ModelLoader.setCustomModelResourceLocation(MetalExtras.LEAD_INGOT, 0, new ModelResourceLocation("metalextras:lead_ingot", "inventory"));
			ModelLoader.setCustomModelResourceLocation(MetalExtras.SILVER_INGOT, 0, new ModelResourceLocation("metalextras:silver_ingot", "inventory"));
			ModelLoader.setCustomModelResourceLocation(MetalExtras.ENDER_GEM, 0, new ModelResourceLocation("metalextras:ender_gem", "inventory"));
			ModelLoader.setCustomModelResourceLocation(MetalExtras.SAPPHIRE_GEM, 0, new ModelResourceLocation("metalextras:sapphire_gem", "inventory"));
			ModelLoader.setCustomModelResourceLocation(MetalExtras.RUBY_GEM, 0, new ModelResourceLocation("metalextras:ruby_gem", "inventory"));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras.COPPER_BLOCK), 0, new ModelResourceLocation("metalextras:copper_block", "normal"));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras.TIN_BLOCK), 0, new ModelResourceLocation("metalextras:tin_block", "normal"));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras.ALUMINUM_BLOCK), 0, new ModelResourceLocation("metalextras:aluminum_block", "normal"));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras.LEAD_BLOCK), 0, new ModelResourceLocation("metalextras:lead_block", "normal"));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras.SILVER_BLOCK), 0, new ModelResourceLocation("metalextras:silver_block", "normal"));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras.ENDER_BLOCK), 0, new ModelResourceLocation("metalextras:ender_block", "normal"));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras.SAPPHIRE_BLOCK), 0, new ModelResourceLocation("metalextras:sapphire_block", "normal"));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(MetalExtras.RUBY_BLOCK), 0, new ModelResourceLocation("metalextras:ruby_block", "normal"));
				for (OreMaterial material : OreUtils.getMaterialsRegistry())
				{
					for (BlockOre block : material.getBlocks())
					{
						StateMapperStatic mapper = StateMapperStatic.create(new ModelResourceLocation(new ResourceLocation("metalextras", "ore"), "normal"));
						ModelLoader.setCustomStateMapper(block, mapper);
						for (int i = 0; i < block.getOreTypeProperty().getAllowedValues().size(); i++)
						{
							Item item = Item.getItemFromBlock(block);
							ModelLoader.setCustomModelResourceLocation(item, i, mapper.getModelLocation(new ItemStack(item, 1, i)));
						}
					}
					ModelLoader.setCustomModelResourceLocation(OreMaterial.ORE, OreUtils.getMaterialsRegistry().getValues().indexOf(material), new ModelResourceLocation(new ResourceLocation("metalextras", material.getRegistryName().getResourcePath() + "_item"), "inventory"));
				}
		
		}
	}
}
