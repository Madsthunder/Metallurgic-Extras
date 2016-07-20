package continuum.metalextras.loaders;

import java.util.ArrayList;
import java.util.HashMap;

import continuum.essentials.mod.CTMod;
import continuum.essentials.mod.ObjectLoader;
import continuum.essentials.util.CreativeTab;
import continuum.metalextras.api.IOre;
import continuum.metalextras.api.OrePredicate;
import continuum.metalextras.api.OreProperties;
import continuum.metalextras.blocks.BlockOreGround;
import continuum.metalextras.blocks.BlockOreGround.EnumGroundType;
import continuum.metalextras.blocks.BlockOreRock;
import continuum.metalextras.blocks.BlockOreRock.EnumRockType;
import continuum.metalextras.mod.MetalExtras_EH;
import continuum.metalextras.mod.MetalExtras_OH;
import continuum.metalextras.world.gen.OreGeneration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class UtilityLoader implements ObjectLoader<MetalExtras_OH, MetalExtras_EH>
{

	@Override
	public void construction(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		holder.oresList = new HashMap<String, IOre[]>();
		holder.orePredicates = new HashMap<String, OrePredicate>();
		holder.ingotList = new HashMap<String, ItemStack>();
		holder.ores = new ArrayList<String>();
		holder.oresToRegen = new ArrayList<String>();
	}
	
	@Override
	public void pre(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		holder.tabOres = new CreativeTab("ores", holder.ender_gem);
		holder.copperOreRock.setCreativeTab(holder.tabOres);
		holder.copperOreGround.setCreativeTab(holder.tabOres);
		holder.tinOreRock.setCreativeTab(holder.tabOres);
		holder.tinOreGround.setCreativeTab(holder.tabOres);
		holder.aluminumOreRock.setCreativeTab(holder.tabOres);
		holder.aluminumOreGround.setCreativeTab(holder.tabOres);
		holder.leadOreRock.setCreativeTab(holder.tabOres);
		holder.leadOreGround.setCreativeTab(holder.tabOres);
		holder.silverOreRock.setCreativeTab(holder.tabOres);
		holder.silverOreGround.setCreativeTab(holder.tabOres);
		holder.sapphireOreRock.setCreativeTab(holder.tabOres);
		holder.sapphireOreGround.setCreativeTab(holder.tabOres);
		holder.rubyOreRock.setCreativeTab(holder.tabOres);
		holder.rubyOreGround.setCreativeTab(holder.tabOres);
		holder.mysteryOreRock.setCreativeTab(holder.tabOres);
		holder.mysteryOreGround.setCreativeTab(holder.tabOres);
		holder.coalOreRock.setCreativeTab(holder.tabOres);
		holder.coalOreGround.setCreativeTab(holder.tabOres);
		holder.ironOreRock.setCreativeTab(holder.tabOres);
		holder.ironOreGround.setCreativeTab(holder.tabOres);
		holder.lapisOreRock.setCreativeTab(holder.tabOres);
		holder.lapisOreGround.setCreativeTab(holder.tabOres);
		holder.goldOreRock.setCreativeTab(holder.tabOres);
		holder.goldOreGround.setCreativeTab(holder.tabOres);
		holder.redstoneOreRock.setCreativeTab(holder.tabOres);
		holder.redstoneOreGround.setCreativeTab(holder.tabOres);
		holder.emeraldOreRock.setCreativeTab(holder.tabOres);
		holder.emeraldOreGround.setCreativeTab(holder.tabOres);
		holder.diamondOreRock.setCreativeTab(holder.tabOres);
		holder.diamondOreGround.setCreativeTab(holder.tabOres);
		holder.copper_block.setCreativeTab(holder.tabOres);
		holder.tin_block.setCreativeTab(holder.tabOres);
		holder.aluminum_block.setCreativeTab(holder.tabOres);
		holder.lead_block.setCreativeTab(holder.tabOres);
		holder.silver_block.setCreativeTab(holder.tabOres);
		holder.ender_block.setCreativeTab(holder.tabOres);
		holder.sapphire_block.setCreativeTab(holder.tabOres);
		holder.ruby_block.setCreativeTab(holder.tabOres);
		
		holder.ore.setCreativeTab(holder.tabOres);
		holder.copper_ingot.setCreativeTab(holder.tabOres);
		holder.tin_ingot.setCreativeTab(holder.tabOres);
		holder.aluminum_ingot.setCreativeTab(holder.tabOres);
		holder.lead_ingot.setCreativeTab(holder.tabOres);
		holder.silver_ingot.setCreativeTab(holder.tabOres);
		holder.ender_gem.setCreativeTab(holder.tabOres);
		holder.sapphire_gem.setCreativeTab(holder.tabOres);
		holder.ruby_gem.setCreativeTab(holder.tabOres);
		
		this.addOreToList(holder, "copper_ore", holder.copperOreRock, holder.copperOreGround, 0, new ItemStack(holder.copper_ingot));
		this.addOreToList(holder, "tin_ore", holder.tinOreRock, holder.tinOreGround, 1, new ItemStack(holder.tin_ingot));
		this.addOreToList(holder, "aluminum_ore", holder.aluminumOreRock, holder.aluminumOreGround, 2, new ItemStack(holder.aluminum_ingot));
		this.addOreToList(holder, "lead_ore", holder.leadOreRock, holder.leadOreGround, 3, new ItemStack(holder.lead_ingot));
		this.addOreToList(holder, "silver_ore", holder.silverOreRock, holder.silverOreGround, 4, new ItemStack(holder.silver_ingot));
		this.addOreToList(holder, "mystery_ore", holder.mysteryOreRock, holder.mysteryOreGround, 5, new ItemStack(holder.ender_gem));
		this.addOreToList(holder, "sapphire_ore", holder.sapphireOreRock, holder.sapphireOreGround, 6, new ItemStack(holder.sapphire_gem));
		this.addOreToList(holder, "ruby_ore", holder.rubyOreRock, holder.rubyOreGround, 7, new ItemStack(holder.ruby_gem));
		this.addOreToList(holder, "coal_ore", holder.coalOreRock, holder.coalOreGround, 8, new ItemStack(Items.COAL), false);
		this.addOreToList(holder, "iron_ore", holder.ironOreRock, holder.ironOreGround, 9, new ItemStack(Items.IRON_INGOT), false);
		this.addOreToList(holder, "lapis_ore", holder.lapisOreRock, holder.lapisOreGround, 10, new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()), false);
		this.addOreToList(holder, "gold_ore", holder.goldOreRock, holder.goldOreGround, 11, new ItemStack(Items.GOLD_INGOT), false);
		this.addOreToList(holder, "redstone_ore", holder.redstoneOreRock, holder.redstoneOreGround, 12, new ItemStack(Items.REDSTONE),false);
		this.addOreToList(holder, "emerald_ore", holder.emeraldOreRock, holder.emeraldOreGround, 13, new ItemStack(Items.EMERALD), false);
		this.addOreToList(holder, "diamond_ore", holder.diamondOreRock, holder.diamondOreGround, 14, new ItemStack(Items.DIAMOND), false);
		
	}

	@Override
	public void init(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		MetalExtras_OH holder = mod.getObjectHolder();
		for(String ore : holder.oresList.keySet())
			MetalExtras_EH.chunkGenerated.put(ore, new ArrayList<Chunk>());
		GameRegistry.registerWorldGenerator(holder.oreGenerator = new OreGeneration(holder), 100);
	}

	@Override
	public void post(CTMod<MetalExtras_OH, MetalExtras_EH> mod)
	{
		
	}
	
	public void addOreToList(MetalExtras_OH objectHolder, String name, BlockOreRock oreRock, BlockOreGround oreGround, Integer propertiesIndex, ItemStack smeltItem)
	{
		this.addOreToList(objectHolder, name, oreRock, oreGround, propertiesIndex, smeltItem, true);
	}
	
	public void addOreToList(MetalExtras_OH objectHolder, String name, BlockOreRock oreRock, BlockOreGround oreGround, Integer propertiesIndex, ItemStack smeltItem, Boolean regen)
	{
		objectHolder.ores.add(name);
		if(regen)
			objectHolder.oresToRegen.add(name);
		objectHolder.oresList.put(name, new IOre[]{oreRock,oreGround});
		OreProperties properties = objectHolder.oreProperties.get(propertiesIndex);
		oreRock.applyOreProperties(properties);
		oreGround.applyOreProperties(properties);
		HashMap<IBlockState, IBlockState> validStates = new HashMap<IBlockState, IBlockState>();
		for(EnumRockType type : EnumRockType.values())
			if(oreRock.canSpawnWithMaterial(type))
				for(IBlockState state : type.states)
					validStates.put(state, oreRock.getDefaultState().withProperty(BlockOreRock.stoneType, type));
		for(EnumGroundType type : EnumGroundType.values())
			if(oreGround.canSpawnWithMaterial(type))
				for(IBlockState state : type.states)
					validStates.put(state, oreGround.getDefaultState().withProperty(BlockOreGround.groundType, type));
		objectHolder.orePredicates.put(name, new OrePredicate(validStates));
		objectHolder.ingotList.put(name, smeltItem);
				
	}
	
	@Override
	public String getName()
	{
		return "Utilities";
	}
}