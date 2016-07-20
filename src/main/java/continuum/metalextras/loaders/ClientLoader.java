package continuum.metalextras.loaders;

import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ObjectLoader;
import continuum.metalextras.blocks.BlockCompressed;
import continuum.metalextras.blocks.BlockOreGround;
import continuum.metalextras.blocks.BlockOreGround.EnumGroundType;
import continuum.metalextras.blocks.BlockOreRock;
import continuum.metalextras.blocks.BlockOreRock.EnumRockType;
import continuum.metalextras.client.state.StateMapperCompressed;
import continuum.metalextras.client.state.StateMapperOre;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientLoader implements ObjectLoader<MetalExtras_OH, MetalExtras_EH>
{
	private IStringSerializable[] strings;
	
	@SideOnly(Side.CLIENT)
	@Override
	public void construction(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		holder.oreStateMapper = new StateMapperOre(holder);
		holder.compressedStateMapper = new StateMapperCompressed();
		this.strings = new IStringSerializable[EnumRockType.values().length + EnumGroundType.values().length];
		for(EnumRockType type : EnumRockType.values())
			this.strings[type.ordinal()] = type;
		for(EnumGroundType type : EnumGroundType.values())
			this.strings[EnumRockType.values().length + type.ordinal()] = type;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void pre(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		registerAllModelsForOre(holder.copperOreRock, holder.copperOreGround, "metalextras:copper_ore", holder.oreStateMapper, holder);
		registerAllModelsForOre(holder.tinOreRock, holder.tinOreGround, "metalextras:tin_ore", holder.oreStateMapper, holder);
		registerAllModelsForOre(holder.aluminumOreRock, holder.aluminumOreGround, "metalextras:aluminum_ore", holder.oreStateMapper, holder);
		registerAllModelsForOre(holder.leadOreRock, holder.leadOreGround, "metalextras:lead_ore", holder.oreStateMapper, holder);
		registerAllModelsForOre(holder.silverOreRock, holder.silverOreGround, "metalextras:silver_ore", holder.oreStateMapper, holder);
		registerAllModelsForOre(holder.mysteryOreRock, holder.mysteryOreGround, "metalextras:mystery_ore", holder.oreStateMapper, holder);
		registerAllModelsForOre(holder.sapphireOreRock, holder.sapphireOreGround, "metalextras:sapphire_ore", holder.oreStateMapper, holder);
		registerAllModelsForOre(holder.rubyOreRock, holder.rubyOreGround, "metalextras:ruby_ore", holder.oreStateMapper, holder);
		registerAllModelsForOre(holder.coalOreRock, holder.coalOreGround, "metalextras:coal_ore", holder.oreStateMapper, holder);
		registerAllModelsForOre(holder.ironOreRock, holder.ironOreGround, "metalextras:iron_ore", holder.oreStateMapper, holder);
		registerAllModelsForOre(holder.lapisOreRock, holder.lapisOreGround, "metalextras:lapis_ore", holder.oreStateMapper, holder);
		registerAllModelsForOre(holder.goldOreRock, holder.goldOreGround, "metalextras:gold_ore", holder.oreStateMapper, holder);
		registerAllModelsForOre(holder.redstoneOreRock, holder.redstoneOreGround, "metalextras:redstone_ore", holder.oreStateMapper, holder);
		registerAllModelsForOre(holder.emeraldOreRock, holder.emeraldOreGround, "metalextras:emerald_ore", holder.oreStateMapper, holder);
		registerAllModelsForOre(holder.diamondOreRock, holder.diamondOreGround, "metalextras:diamond_ore", holder.oreStateMapper, holder);
		ModelLoader.setCustomModelResourceLocation(holder.copper_ingot, 0, new ModelResourceLocation("metalextras:copper_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(holder.tin_ingot, 0, new ModelResourceLocation("metalextras:tin_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(holder.aluminum_ingot, 0, new ModelResourceLocation("metalextras:aluminum_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(holder.lead_ingot, 0, new ModelResourceLocation("metalextras:lead_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(holder.silver_ingot, 0, new ModelResourceLocation("metalextras:silver_ingot", "inventory"));
		ModelLoader.setCustomModelResourceLocation(holder.ender_gem, 0, new ModelResourceLocation("metalextras:ender_gem", "inventory"));
		ModelLoader.setCustomModelResourceLocation(holder.sapphire_gem, 0, new ModelResourceLocation("metalextras:sapphire_gem", "inventory"));
		ModelLoader.setCustomModelResourceLocation(holder.ruby_gem, 0, new ModelResourceLocation("metalextras:ruby_gem", "inventory"));
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.copper_block);
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.tin_block);
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.aluminum_block);
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.lead_block);
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.silver_block);
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.ender_block);
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.sapphire_block);
		registerAllModelsForCompressed(holder.compressedStateMapper, holder.ruby_block);
	}

	@SideOnly(Side.CLIENT)
	public void registerAllModelsForOre(BlockOreRock rockOre, BlockOreGround groundOre, String name, IStateMapper mapper, MetalExtras_OH holder)
	{
		this.registerModelsForOre(rockOre, name, mapper, EnumRockType.values());
		this.registerModelsForOre(groundOre, name, mapper, EnumGroundType.values());
		ModelLoader.setCustomModelResourceLocation(holder.ore, holder.ores.indexOf(name.substring(12)), new ModelResourceLocation(name + "_item", "inventory"));
	}
	
	@SideOnly(Side.CLIENT)
	public void registerAllModelsForCompressed(IStateMapper mapper, BlockCompressed block)
	{
		ModelLoader.setCustomStateMapper(block, mapper);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation("metalextras:compressed", block.getUnlocalizedName().substring(5)));
	}
	
	@SideOnly(Side.CLIENT)
	public void registerModelsForOre(Block block, String name, IStateMapper mapper, IStringSerializable[] strings)
	{
		ModelLoader.setCustomStateMapper(block, mapper);
		for(Integer i=0;i<strings.length;i++)
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(name, "type=" + strings[i].getName()));
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