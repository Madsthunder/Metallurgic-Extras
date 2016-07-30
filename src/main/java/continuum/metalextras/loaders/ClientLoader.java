package continuum.metalextras.loaders;

import java.util.List;

import continuum.api.metalextras.IOreData;
import continuum.essentials.helpers.ObjectHelper;
import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ObjectLoader;
import continuum.metalextras.blocks.BlockCompressed;
import continuum.metalextras.blocks.BlockOre;
import continuum.metalextras.client.state.StateMapperCompressed;
import continuum.metalextras.client.state.StateMapperOre;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientLoader implements ObjectLoader<MetalExtras_OH, MetalExtras_EH>
{	
	@SideOnly(Side.CLIENT)
	@Override
	public void construction(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		holder.oreStateMapper = new StateMapperOre(holder);
		holder.compressedStateMapper = new StateMapperCompressed();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void pre(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		ModelLoader.setCustomModelResourceLocation(holder.copper_ingot, 0, new ModelResourceLocation("metalextras:copper_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(holder.tin_ingot, 0, new ModelResourceLocation("metalextras:tin_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(holder.aluminum_ingot, 0, new ModelResourceLocation("metalextras:aluminum_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(holder.lead_ingot, 0, new ModelResourceLocation("metalextras:lead_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(holder.silver_ingot, 0, new ModelResourceLocation("metalextras:silver_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(holder.ender_gem, 0, new ModelResourceLocation("metalextras:ender_gem", "inventory"));
		ModelLoader.setCustomModelResourceLocation(holder.sapphire_gem, 0, new ModelResourceLocation("metalextras:sapphire_gem", "inventory"));
		ModelLoader.setCustomModelResourceLocation(holder.ruby_gem, 0, new ModelResourceLocation("metalextras:ruby_gem", "inventory"));
		for(IOreData data : MetalExtras_OH.ores)
			this.registerAllModelsForOre(data, holder);
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.copper_block);
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.tin_block);
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.aluminum_block);
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.lead_block);
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.silver_block);
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.ender_block);
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.sapphire_block);
	}

	@SideOnly(Side.CLIENT)
	public void registerAllModelsForOre(IOreData data, MetalExtras_OH holder)
	{
		for(BlockOre ore : data.getOre().getOreBlocks())
			this.registerModelsForOre(ore, holder.oreStateMapper, ore.getOreTypeProperty().getAllowedValues());
		ModelLoader.setCustomModelResourceLocation(holder.ore, holder.ores.indexOf(data), new ModelResourceLocation(new ResourceLocation("metalextras", data.getOreName().getResourcePath() + "_item"), "inventory"));
	}
	
	@SideOnly(Side.CLIENT)
	public void registerAllModelsForCompressed(IStateMapper mapper, BlockCompressed block)
	{
		ModelLoader.setCustomStateMapper(block, mapper);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation("metalextras:compressed", block.getUnlocalizedName().substring(5)));
	}
	
	@SideOnly(Side.CLIENT)
	public void registerModelsForOre(BlockOre block, IStateMapper mapper, List<? extends IStringSerializable> strings)
	{
		ModelLoader.setCustomStateMapper(block, mapper);
		for(Integer i : ObjectHelper.increment(strings.size()))
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(new ResourceLocation("metalextras", block.getOreData().getOreName().getResourcePath()), "type=" + strings.get(i).getName()));
	}
	
	@Override
	public String getName()
	{
		return "Client";
	}

	@Override
	public Side getSide()
	{
		return Side.CLIENT;
	}

}