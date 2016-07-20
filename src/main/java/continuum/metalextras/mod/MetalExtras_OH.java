package continuum.metalextras.mod;

import java.util.ArrayList;
import java.util.HashMap;

import continuum.essentials.mod.ObjectHolder;
import continuum.metalextras.api.IOre;
import continuum.metalextras.api.OrePredicate;
import continuum.metalextras.api.OreProperties;
import continuum.metalextras.blocks.BlockCompressed;
import continuum.metalextras.blocks.BlockOreGround;
import continuum.metalextras.blocks.BlockOreRock;
import continuum.metalextras.client.state.StateMapperCompressed;
import continuum.metalextras.client.state.StateMapperOre;
import continuum.metalextras.items.ItemBlockCompressed;
import continuum.metalextras.items.ItemOre;
import continuum.metalextras.world.gen.OreGeneration;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
	
	public BlockOreRock copperOreRock;
	public BlockOreGround copperOreGround;
	public BlockOreRock tinOreRock;
	public BlockOreGround tinOreGround;
	public BlockOreRock aluminumOreRock;
	public BlockOreGround aluminumOreGround;
	public BlockOreRock leadOreRock;
	public BlockOreGround leadOreGround;
	public BlockOreRock silverOreRock;
	public BlockOreGround silverOreGround;
	public BlockOreRock mysteryOreRock;
	public BlockOreGround mysteryOreGround;
	public BlockOreRock sapphireOreRock;
	public BlockOreGround sapphireOreGround;
	public BlockOreRock rubyOreRock;
	public BlockOreGround rubyOreGround;
	public BlockOreRock coalOreRock;
	public BlockOreGround coalOreGround;
	public BlockOreRock ironOreRock;
	public BlockOreGround ironOreGround;
	public BlockOreRock lapisOreRock;
	public BlockOreGround lapisOreGround;
	public BlockOreRock goldOreRock;
	public BlockOreGround goldOreGround;
	public BlockOreRock redstoneOreRock;
	public BlockOreGround redstoneOreGround;
	public BlockOreRock emeraldOreRock;
	public BlockOreGround emeraldOreGround;
	public BlockOreRock diamondOreRock;
	public BlockOreGround diamondOreGround;
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
	
	public ArrayList<String> ores;
	public ArrayList<String> oresToRegen;
	public HashMap<String, ItemStack> ingotList;
	public HashMap<String, ItemStack> compressedList;
	public HashMap<String, IOre[]> oresList;
	public HashMap<String, OrePredicate> orePredicates;
	
	public OreGeneration oreGenerator;
	@SideOnly(value = Side.CLIENT)
	public StateMapperOre oreStateMapper;
	@SideOnly(value = Side.CLIENT)
	public StateMapperCompressed compressedStateMapper;
}
