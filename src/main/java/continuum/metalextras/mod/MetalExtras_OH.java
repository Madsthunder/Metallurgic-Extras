package continuum.metalextras.mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;

import continuum.api.metalextras.IOreData;
import continuum.api.metalextras.IOreGroup;
import continuum.api.metalextras.IOreType;
import continuum.api.metalextras.OrePredicate;
import continuum.api.metalextras.OreProperties;
import continuum.essentials.mod.ObjectHolder;
import continuum.metalextras.blocks.BlockCompressed;
import continuum.metalextras.client.state.StateMapperCompressed;
import continuum.metalextras.client.state.StateMapperOre;
import continuum.metalextras.items.ItemOre;
import continuum.metalextras.world.gen.OreGeneration;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetalExtras_OH implements ObjectHolder
{

	static MetalExtras_OH getHolder()
	{
		return new MetalExtras_OH();
	}
	@Override
	public String getModid() 
	{
		return "MetalExtras";
	}

	@Override
	public String getName() 
	{
		return "Metallurgic Extras";
	}

	@Override
	public String getVersion() 
	{
		return "1.2.1";
	}
	
	public CreativeTabs tabOres;
	
	public BlockCompressed copper_block;
	public BlockCompressed tin_block;
	public BlockCompressed aluminum_block;
	public BlockCompressed lead_block;
	public BlockCompressed silver_block;
	public BlockCompressed ender_block;
	public BlockCompressed sapphire_block;
	public BlockCompressed ruby_block;
	
	public ItemOre ore;
	public Item copper_ingot;
	public Item tin_ingot;
	public Item aluminum_ingot;
	public Item lead_ingot;
	public Item silver_ingot;
	public Item ender_gem;
	public Item sapphire_gem;
	public Item ruby_gem;
	
	public ArrayList<OreProperties> oreProperties;
	
	public static final List<IOreType> oreTypes = Lists.newArrayList(new IOreType.Impl("stone", "rock", Blocks.STONE.getDefaultState(), 1.5F, 10F));
	public static final List<IOreGroup> oreGroups = Lists.newArrayList();
	public static final ArrayList<IOreData> ores = Lists.newArrayList();
	public List<ResourceLocation> oresToReplace;
	public HashMap<ResourceLocation, ItemStack> ingotList;
	public HashMap<ResourceLocation, ItemStack> compressedList;
	public HashMap<ResourceLocation, OrePredicate> orePredicates;
	
	public OreGeneration oreGenerator;
	@SideOnly(value = Side.CLIENT)
	public StateMapperOre oreStateMapper;
	@SideOnly(value = Side.CLIENT)
	public StateMapperCompressed compressedStateMapper;
}
